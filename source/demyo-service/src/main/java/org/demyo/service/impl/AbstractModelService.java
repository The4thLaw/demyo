package org.demyo.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.demyo.common.exception.DemyoErrorCode;
import org.demyo.common.exception.DemyoRuntimeException;
import org.demyo.dao.IModelRepo;
import org.demyo.dao.IQuickSearchableRepo;
import org.demyo.model.IModel;
import org.demyo.model.util.DefaultOrder;
import org.demyo.model.util.PreSave;
import org.demyo.service.IConfigurationService;
import org.demyo.service.IModelService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of base operations on models.
 * 
 * @param <M> The model type.
 */
public abstract class AbstractModelService<M extends IModel> implements IModelService<M> {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractModelService.class);

	@Autowired
	private IConfigurationService configurationService;

	private final Class<M> modelClass;
	/**
	 * The default order specified by the {@link IModel}, as a Spring Data-compatible {@link Order}.
	 */
	private final Order[] defaultOrder;

	private final Method[] getterMethods;
	private final Method[] setterMethods;
	private final Method[] preSaveMethods;

	/**
	 * Creates an abstract model service.
	 * 
	 * @param modelClass The class of the model to work on.
	 */
	protected AbstractModelService(Class<M> modelClass) {
		this.modelClass = modelClass;

		this.defaultOrder = loadDefaultOrder();

		// Detect methods to clear linked models or to call before save
		List<Method> getters = new ArrayList<>();
		List<Method> setters = new ArrayList<>();
		List<Method> preSave = new ArrayList<>();
		for (Method meth : modelClass.getMethods()) {
			boolean isModelGetter = IModel.class.isAssignableFrom(meth.getReturnType())
					&& meth.getName().startsWith("get") && meth.getParameterTypes().length == 0;
			boolean isTransientMethod = meth.getAnnotation(Transient.class) != null;
			if (isModelGetter && !"getClass".equals(meth.getName()) && !isTransientMethod) {
				// This is a standard getter method for a linked model
				getters.add(meth);
				Method setter;
				try {
					setter = modelClass.getMethod(meth.getName().replaceFirst("get", "set"), meth.getReturnType());
				} catch (NoSuchMethodException | SecurityException e) {
					throw new DemyoRuntimeException(DemyoErrorCode.ORM_INVALID_PROPERTY, e,
							"No setter for getter", meth.getName());
				}
				setters.add(setter);
			}

			loadPreSaveMethod(preSave, meth);
		}

		getterMethods = getters.toArray(new Method[getters.size()]);
		setterMethods = setters.toArray(new Method[setters.size()]);
		preSaveMethods = preSave.toArray(new Method[preSave.size()]);

		LOGGER.debug("Default order set for {}", modelClass);
	}

	private Order[] loadDefaultOrder() {
		Order[] loadedOrder;

		DefaultOrder defaultOrderAnnotation = modelClass.getAnnotation(DefaultOrder.class);
		if (defaultOrderAnnotation != null && defaultOrderAnnotation.expression().length > 0) {
			org.demyo.model.util.DefaultOrder.Order[] defaultOrderExpression = defaultOrderAnnotation.expression();

			loadedOrder = new Order[defaultOrderExpression.length];

			// Convert the default order to expressions that can be handled later. Preserve the order of the annotation.
			for (int i = 0; i < defaultOrderExpression.length; i++) {
				org.demyo.model.util.DefaultOrder.Order order = defaultOrderExpression[i];
				loadedOrder[i] = new Order(order.asc() ? Direction.ASC : Direction.DESC, order.property());
			}
		} else {
			loadedOrder = null;
		}

		return loadedOrder;
	}

	private static void loadPreSaveMethod(List<Method> preSave, Method meth) {
		if (meth.getAnnotation(PreSave.class) != null) {
			if (meth.getParameterTypes().length != 0) {
				throw new DemyoRuntimeException(DemyoErrorCode.ORM_INVALID_PRESAVE,
						"@PreSave methods cannot have parameters", meth.getName());
			}
			if (!meth.getReturnType().equals(Void.TYPE)) {
				throw new DemyoRuntimeException(DemyoErrorCode.ORM_INVALID_PRESAVE,
						"@PreSave methods cannot have return values", meth.getName(), meth.getReturnType()
								.toString());
			}
			preSave.add(meth);
		}
	}

	/**
	 * Gets the DAO for this model.
	 * 
	 * @return the DAO.
	 */
	protected abstract IModelRepo<M> getRepo();

	/** {@inheritDoc} By default, this method delegates to {@link #getByIdForEdition(long)}. */
	@Transactional(rollbackFor = Throwable.class)
	@Override
	public M getByIdForView(long id) {
		return getByIdForEdition(id);
	}

	@Transactional(rollbackFor = Throwable.class)
	@Override
	public M getByIdForEdition(long id) {
		return getRepo().findOneForEdition(id);
	}

	@Transactional(readOnly = true)
	@Override
	public int getFirstPageForLetter(char startsWith) {
		throw new UnsupportedOperationException("Not migrated to Spring Data yet");
		/*return getDao().getFirstPageForLetter(startsWith,
				configurationService.getConfiguration().getPageSizeForText());*/
	}

	@Transactional(readOnly = true)
	@Override
	public List<M> findAll() {
		Sort sort = defaultOrder.length == 0 ? null : new Sort(defaultOrder);
		return getRepo().findAll(sort);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * The default implementation uses the amount of items per page of text. If the service needs to change that
	 * amount (e.g. for pages of images), it should override this method.
	 * </p>
	 */
	@Transactional(readOnly = true)
	@Override
	public Slice<M> findPaginated(int currentPage, Order... orders) {
		Pageable pageable = getPageable(currentPage, orders);
		return getRepo().findAll(pageable);
	}

	/**
	 * Gets a Spring Data {@link Pageable} object from the current page and orders.
	 * 
	 * @param currentPage The page number (starting at 1).
	 * @param orders Ordering of the result set. May be <code>null</code> to use the default ordering. If no
	 *        default ordering is defined, the ordering is defined by the database.
	 * @return The {@link Pageable} instance
	 */
	protected Pageable getPageable(int currentPage, Order... orders) {
		// Adjust the page number: Spring Data counts from 0
		currentPage--;

		if (orders.length == 0) {
			orders = defaultOrder;
		}
		Sort sort = orders.length == 0 ? null : new Sort(orders);
		Pageable pageable = new PageRequest(currentPage, configurationService.getConfiguration()
				.getPageSizeForText(), sort);
		return pageable;
	}

	@Transactional(rollbackFor = Throwable.class)
	@Override
	public long save(@NotNull M model) {
		// Call PreSave methods
		for (Method meth : preSaveMethods) {
			try {
				meth.invoke(model, new Object[] {});
				LOGGER.debug("Called @PreSave method {}", meth);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new DemyoRuntimeException(DemyoErrorCode.ORM_INVALID_PRESAVE, e,
						"Calling @PreSave method failed", meth.getName());
			}
		}

		model = reloadIfNeeded(model);

		// Before saving, we must remove any linked models that have a null id. These are models that should not exist.
		// For example, this happens when trying to save an Album that has no binding. It is still applicable with
		// Spring Data
		for (int i = 0; i < getterMethods.length; i++) {
			Method getter = getterMethods[i];
			try {
				IModel other = (IModel) getter.invoke(model);
				if (other != null && other.getId() == null) {
					Method setter = setterMethods[i];
					setter.invoke(model, new Object[] { null });
					LOGGER.debug("Cleared linked model: {}.{}()", modelClass.getName(), getter.getName());
				}
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| SecurityException e) {
				LOGGER.warn("Failed to clear linked model", e);
			}
		}

		M savedModel = getRepo().save(model);
		return savedModel.getId();
	}

	/**
	 * Reloads the model from the database if needed. For example, this is required if the model manages orphaned
	 * collections of child entities.
	 * <p>
	 * This method is responsible of applying the properties from the provided model to the returned model.
	 * </p>
	 * <p>
	 * By default, this method returns the provided object without any modification.
	 * </p>
	 * 
	 * @param model The model that would be saved.
	 * @return The model to save.
	 */
	protected M reloadIfNeeded(M model) {
		return model;
	}

	@Transactional(rollbackFor = Throwable.class)
	@Override
	public void delete(long id) {
		getRepo().delete(id);
	}

	/**
	 * Gets the default order specified by the {@link IModel}, as a Spring Data-compatible {@link Order}.
	 * 
	 * @return the default order specified by the {@link IModel}, as a Spring Data-compatible {@link Order}
	 */
	protected Order[] getDefaultOrder() {
		return defaultOrder;
	}

	/**
	 * Base implementation of a quick search query on an {@link IQuickSearchableRepo}.
	 * 
	 * @param query The string to match
	 * @param exact <code>true</code> if the search is for an exact match. <code>false</code> otherwise.
	 * @param searchRepo The repository to search on.
	 * @return The matches, as a future.
	 */
	protected Future<List<M>> quickSearch(String query, boolean exact, IQuickSearchableRepo<M> searchRepo) {
		if (exact) {
			return new AsyncResult<>(searchRepo.quickSearchExact(query));
		}
		return new AsyncResult<>(searchRepo.quickSearchLike(query));
	}
}
