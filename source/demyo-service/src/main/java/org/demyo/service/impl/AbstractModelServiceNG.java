package org.demyo.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.demyo.model.IModel;
import org.demyo.service.IConfigurationService;
import org.demyo.service.IModelServiceNG;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of base operations on models.
 * 
 * @author $Author: xr $
 * @version $Revision: 1084 $
 * @param <M>
 */
public abstract class AbstractModelServiceNG<M extends IModel> implements IModelServiceNG<M> {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractModelServiceNG.class);

	@Autowired
	private IConfigurationService configurationService;

	private final Class<M> modelClass;

	/**
	 * Creates an abstract model service.
	 * 
	 * @param modelClass The class of the model to work on.
	 */
	protected AbstractModelServiceNG(Class<M> modelClass) {
		this.modelClass = modelClass;
	}

	/**
	 * Gets the DAO for this model.
	 * 
	 * @return the DAO.
	 */
	//protected abstract IModelRepo<M> getRepo();
	protected abstract JpaRepository<M, Long> getRepo();

	/** {@inheritDoc} By default, this method delegates to {@link #getByIdForEdition(long)}. */
	@Transactional(rollbackFor = Throwable.class)
	@Override
	public M getByIdForView(long id) {
		return getByIdForEdition(id);
	}

	@Transactional(rollbackFor = Throwable.class)
	@Override
	public M getByIdForEdition(long id) {
		// TODO: load all lazy associations
		// not great: https://stackoverflow.com/questions/15359306/how-to-load-lazy-fetched-items-from-hibernate-jpa-in-my-controller
		// looks good: https://stackoverflow.com/questions/29602386/how-does-the-fetchmode-work-in-spring-data-jpa
		// http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.entity-graph
		return getRepo().findOne(id);
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
		return getRepo().findAll();
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

		Sort sort = orders.length == 0 ? null : new Sort(orders);
		Pageable pageable = new PageRequest(currentPage, configurationService.getConfiguration()
				.getPageSizeForText(), sort);
		// TODO: find how to force this to return a slice rather than a page
		return getRepo().findAll(pageable);
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

		M savedModel = getRepo().save(model);
		return savedModel.getId();
	}

	@Transactional(rollbackFor = Throwable.class)
	@Override
	public void delete(long id) {
		getRepo().delete(id);
	}
}
