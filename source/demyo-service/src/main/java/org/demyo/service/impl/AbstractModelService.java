package org.demyo.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.demyo.dao.IModelDao;
import org.demyo.dao.JoinTypeHolder;
import org.demyo.model.IModel;
import org.demyo.service.IConfigurationService;
import org.demyo.service.IModelService;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of base operations on models.
 */
@Deprecated
public abstract class AbstractModelService<M extends IModel> implements IModelService<M> {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractModelService.class);

	@Autowired
	private IConfigurationService configurationService;

	private final Class<M> modelClass;

	/**
	 * Creates an abstract model service.
	 * 
	 * @param modelClass The class of the model to work on.
	 */
	protected AbstractModelService(Class<M> modelClass) {
		this.modelClass = modelClass;
	}

	/**
	 * Gets the DAO for this model.
	 * 
	 * @return the DAO.
	 */
	protected abstract IModelDao<M> getDao();

	/** {@inheritDoc} By default, this method delegates to {@link #getByIdForEdition(long)}. */
	@Transactional(rollbackFor = Throwable.class)
	@Override
	public M getByIdForView(long id) {
		return getByIdForEdition(id);
	}

	@Transactional(rollbackFor = Throwable.class)
	@Override
	public M getByIdForEdition(long id) {
		return getDao().findById(id, true);
	}

	@Transactional(readOnly = true)
	@Override
	public int getFirstPageForLetter(char startsWith) {
		return getDao().getFirstPageForLetter(startsWith,
				configurationService.getConfiguration().getPageSizeForText());
	}

	@Transactional(readOnly = true)
	@Override
	public List<M> findAll() {
		return getDao().findAll();
	}

	@Transactional(readOnly = true)
	@Override
	public List<M> findAll(Criterion criterion) {
		return getDao().findAll(criterion);
	}

	@Transactional(readOnly = true)
	@Override
	public List<M> findPaginated(int currentPage) {
		return findPaginated(currentPage, null, null);
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
	public List<M> findPaginated(int currentPage, Criterion criterion, JoinTypeHolder fetchModes, Order... orders) {
		return getDao().findPaginated(currentPage, configurationService.getConfiguration().getPageSizeForText(),
				criterion, fetchModes, orders);
	}

	@Transactional(rollbackFor = Throwable.class)
	@Override
	public long save(M model) {
		// Before saving, we must remove any linked models that have a null id. These are models that should not exist
		for (Method meth : modelClass.getMethods()) {
			if (IModel.class.isAssignableFrom(meth.getReturnType()) && meth.getName().startsWith("get")
					&& !"getClass".equals(meth.getName()) && meth.getParameterTypes().length == 0) {
				// This is a standard getter method for a linked model
				try {
					IModel other = (IModel) meth.invoke(model);
					if (other.getId() == null) {
						Method setter = modelClass.getMethod(meth.getName().replaceFirst("get", "set"),
								meth.getReturnType());
						setter.invoke(model, new Object[] { null });
					}
					LOGGER.debug("Cleared linked model: {}.{}()", modelClass.getName(), meth.getName());
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
						| NoSuchMethodException | SecurityException e) {
					LOGGER.warn("Failed to clear linked model", e);
				}
			}
		}

		return getDao().save(model);
	}

	@Transactional(rollbackFor = Throwable.class)
	@Override
	public void delete(long id) {
		getDao().delete(id);
	}
}
