package org.demyo.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.demyo.dao.IModelRepo;
import org.demyo.model.IModel;
import org.demyo.model.util.DefaultOrder;
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
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of base operations on models.
 * 
 * @param <M>
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

	/**
	 * Creates an abstract model service.
	 * 
	 * @param modelClass The class of the model to work on.
	 */
	protected AbstractModelService(Class<M> modelClass) {
		this.modelClass = modelClass;

		// Detect default order
		DefaultOrder defaultOrderAnnotation = modelClass.getAnnotation(DefaultOrder.class);
		if (defaultOrderAnnotation != null && defaultOrderAnnotation.expression().length > 0) {
			org.demyo.model.util.DefaultOrder.Order[] defaultOrderExpression = defaultOrderAnnotation.expression();

			defaultOrder = new Order[defaultOrderExpression.length];

			// Convert the default order to expressions that can be handled later. Preserve the order of the annotation.
			for (int i = 0; i < defaultOrderExpression.length; i++) {
				org.demyo.model.util.DefaultOrder.Order order = defaultOrderExpression[i];
				defaultOrder[i] = new Order(order.asc() ? Direction.ASC : Direction.DESC, order.property());
			}
		} else {
			defaultOrder = null;
		}
		LOGGER.debug("Default order set for {}", modelClass);
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
		// Adjust the page number: Spring Data counts from 0
		currentPage--;

		if (orders.length == 0) {
			orders = defaultOrder;
		}
		Sort sort = orders.length == 0 ? null : new Sort(orders);
		Pageable pageable = new PageRequest(currentPage, configurationService.getConfiguration()
				.getPageSizeForText(), sort);
		return getRepo().findAll(pageable);
	}

	@Transactional(rollbackFor = Throwable.class)
	@Override
	public long save(M model) {
		// TODO: figure out if this is still needed with Spring Data
		// Before saving, we must remove any linked models that have a null id. These are models that should not exist
		for (Method meth : modelClass.getMethods()) {
			if (IModel.class.isAssignableFrom(meth.getReturnType()) && meth.getName().startsWith("get")
					&& !"getClass".equals(meth.getName()) && meth.getParameterTypes().length == 0) {
				// This is a standard getter method for a linked model
				try {
					IModel other = (IModel) meth.invoke(model);
					if (other != null && other.getId() == null) {
						Method setter = modelClass.getMethod(meth.getName().replaceFirst("get", "set"),
								meth.getReturnType());
						setter.invoke(model, new Object[] { null });
						LOGGER.debug("Cleared linked model: {}.{}()", modelClass.getName(), meth.getName());
					}
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
						| NoSuchMethodException | SecurityException e) {
					LOGGER.warn("Failed to clear linked model", e);
				}
			}
		}

		M savedModel = getRepo().save(model);
		return savedModel.getId();
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
}
