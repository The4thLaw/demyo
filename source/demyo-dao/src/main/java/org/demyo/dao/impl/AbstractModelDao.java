package org.demyo.dao.impl;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.demyo.dao.IModelDao;
import org.demyo.dao.JoinTypeHolder;
import org.demyo.model.IModel;
import org.demyo.model.exception.DemyoErrorCode;
import org.demyo.model.exception.DemyoRuntimeException;
import org.demyo.model.util.DefaultOrder;
import org.demyo.model.util.PaginatedList;
import org.demyo.model.util.StartsWithField;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for DAO's.
 * 
 * @author $Author: xr $
 * @version $Revision: 1083 $
 * @param <M> The type of the model.
 */
public abstract class AbstractModelDao<M extends IModel> implements IModelDao<M> {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractModelDao.class);

	/** The entity manager. */
	@PersistenceContext
	private EntityManager entityManager;

	private final Class<M> modelClass;
	private final String className;
	private final String defaultOrderAsString;
	/**
	 * The default order specified by the {@link IModel}, as a Hibernate {@link Criteria}-compatible {@link Order}.
	 */
	private final Order[] defaultOrderAsCriteria;
	private final String startsWithFieldName;

	/**
	 * Creates an abstract DAO.
	 * 
	 * @param modelClass The class of the model to work on.
	 */
	protected AbstractModelDao(Class<M> modelClass) {
		this.modelClass = modelClass;
		className = modelClass.getSimpleName();

		// Detect default order
		DefaultOrder defaultOrder = modelClass.getAnnotation(DefaultOrder.class);
		if (defaultOrder != null && defaultOrder.expression().length > 0) {
			org.demyo.model.util.DefaultOrder.Order[] defaultOrderExpression = defaultOrder.expression();

			defaultOrderAsCriteria = new Order[defaultOrderExpression.length];
			StringBuilder sb = new StringBuilder();

			// Convert the default order to expressions that can be handled later. Preserve the order of the annotation.
			for (int i = 0; i < defaultOrderExpression.length; i++) {
				org.demyo.model.util.DefaultOrder.Order order = defaultOrderExpression[i];

				if (order.asc()) {
					sb.append(order.property()).append(" asc");
					defaultOrderAsCriteria[i] = Order.asc(order.property());
				} else {
					sb.append(order.property()).append(" desc");
					defaultOrderAsCriteria[i] = Order.desc(order.property());
				}

				if (i < defaultOrderExpression.length - 1) {
					sb.append(", ");
				}
			}
			defaultOrderAsString = sb.toString();
		} else {
			defaultOrderAsString = null;
			defaultOrderAsCriteria = null;
		}
		LOGGER.debug("Default order for {}: {}", modelClass, defaultOrderAsString);

		// Detect startsWith field
		Field startsWithField = null;
		for (Field field : modelClass.getDeclaredFields()) {
			if (field.getAnnotation(StartsWithField.class) != null) {
				startsWithField = field;
			}
		}
		startsWithFieldName = startsWithField == null ? null : startsWithField.getName();
		LOGGER.debug("'startsWith' field for {}: {}", modelClass, startsWithFieldName);
	}

	@Override
	public M findById(long id, boolean loadLazy) {
		TypedQuery<M> query = entityManager.createQuery("FROM " + className + " WHERE id=:id", modelClass);
		query.setParameter("id", id);
		M singleResult = query.getSingleResult();

		if (loadLazy) {
			for (Field f : modelClass.getDeclaredFields()) {
				OneToMany oneToMany = f.getAnnotation(OneToMany.class);
				ManyToOne manyToOne = f.getAnnotation(ManyToOne.class);
				ManyToMany manyToMany = f.getAnnotation(ManyToMany.class);
				if ((oneToMany != null && FetchType.LAZY.equals((oneToMany.fetch())))
						|| (manyToOne != null && FetchType.LAZY.equals(manyToOne.fetch()))
						|| (manyToMany != null && FetchType.LAZY.equals(manyToMany.fetch()))) {
					try {
						Object property = PropertyUtils.getProperty(singleResult, f.getName());
						if (property instanceof IModel) {
							((IModel) property).getId();
						} else if (property instanceof Collection) {
							((Collection<?>) property).size();
						}
					} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
						LOGGER.error("Failed to fetch lazy relationships for {} with id {}", modelClass, id, e);
					}
				}
			}
		}

		return singleResult;
	}

	@Override
	public int getFirstPageForLetter(char startsWith, int pageSize) {
		if (startsWithFieldName == null) {
			throw new DemyoRuntimeException(DemyoErrorCode.PAGING_NO_STARTSWITH);
		}

		String queryString = "SELECT COUNT(*) FROM " + className + " WHERE " + startsWithFieldName
				+ " < :startsWith";
		Query query = getEntityManager().createQuery(queryString);
		query.setParameter("startsWith", String.valueOf(startsWith));
		long countBefore = (Long) query.getSingleResult();

		LOGGER.trace("There are {} records before letter {}", countBefore, startsWith);

		return (int) Math.floor(1 + countBefore / (double) pageSize);
	}

	@Override
	public List<M> findAll() {
		String queryString = "FROM " + className;
		if (defaultOrderAsString != null) {
			queryString += " ORDER BY " + defaultOrderAsString;
		}
		TypedQuery<M> query = getEntityManager().createQuery(queryString, modelClass);
		return query.getResultList();
	}

	@Override
	public List<M> findAll(Criterion criterion) {
		return findAllInternal(null, null, criterion, null, (Order[]) null);
	}

	@Override
	public List<M> findAll(Criterion criterion, JoinTypeHolder fetchModes, Order... orders) {
		return findAllInternal(null, null, criterion, fetchModes, orders);
	}

	@Override
	public final PaginatedList<M> findPaginated(int currentPage, int pageSize) {
		return findPaginated(currentPage, pageSize, null, null, (Order[]) null);
	}

	@Override
	public final PaginatedList<M> findPaginated(int currentPage, int pageSize, Criterion criterion) {
		return findPaginated(currentPage, pageSize, criterion, null, (Order[]) null);
	}

	@Override
	public final PaginatedList<M> findPaginated(int currentPage, int pageSize, Criterion criterion,
			Order... orders) {
		return findPaginated(currentPage, pageSize, criterion, null, orders);
	}

	// Documentation of Criteria is at https://docs.jboss.org/hibernate/orm/4.1/manual/en-US/html/ch17.html
	@Override
	public PaginatedList<M> findPaginated(int currentPage, int pageSize, Criterion criterion,
			JoinTypeHolder fetchModes, Order... orders) {
		return findAllInternal(currentPage, pageSize, criterion, fetchModes, orders);
	}

	/**
	 * Same behaviour as {@link #findPaginated(int, int, Criterion, JoinTypeHolder, Order...)}, except that the
	 * list is not paginated if {@code currentPage} or {@code pageSize} is <code>null</code>.
	 */
	private PaginatedList<M> findAllInternal(Integer currentPage, Integer pageSize, Criterion criterion,
			JoinTypeHolder fetchModes, Order... orders) {
		Criteria criteria = getBaseCriteria(criterion, fetchModes);

		if (orders == null || orders.length == 0) {
			orders = defaultOrderAsCriteria;
		}
		if (orders != null) { // The default might still be null
			for (Order order : orders) {
				criteria.addOrder(order);
			}
		}

		if (currentPage != null && pageSize != null) {
			criteria.setFirstResult((currentPage - 1) * pageSize);
			criteria.setMaxResults(pageSize);
		}

		@SuppressWarnings("unchecked")
		List<M> result = criteria.list();

		if (currentPage != null || pageSize != null) {
			// Compute total
			Criteria countCriteria = getBaseCriteria(criterion, fetchModes);
			countCriteria.setProjection(Projections.rowCount());
			long total = (Long) countCriteria.list().get(0);

			return new PaginatedList<M>(result, currentPage, pageSize, total);
		} else {
			return new PaginatedList<M>(result, -1, Integer.MAX_VALUE, -1);
		}
	}

	private Criteria getBaseCriteria(Criterion criterion, JoinTypeHolder fetchModes) {
		Criteria criteria = getSession().createCriteria(modelClass);

		if (criterion != null) {
			criteria.add(criterion);
		}

		if (fetchModes != null) {
			fetchModes.populateCriteria(criteria);
			// See http://stackoverflow.com/a/1995244 and 
			// https://developer.jboss.org/wiki/HibernateFAQ-AdvancedProblems?_sscc=t
			//		#jive_content_id_Hibernate_does_not_return_distinct_results_for_a_query_with_outer_join_fetching_
			//		enabled_for_a_collection_even_if_I_use_the_distinct_keyword
			criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		}
		return criteria;
	}

	@Override
	public long save(M model) {
		M savedModel = getEntityManager().merge(model);
		return savedModel.getId();
	}

	@Override
	public void delete(long id) {
		Query query = getEntityManager().createQuery("DELETE FROM " + className + " WHERE id=:id");
		query.setParameter("id", id);
		query.executeUpdate();
	}

	/**
	 * Gets the entity manager.
	 * 
	 * @return the entity manager
	 */
	public EntityManager getEntityManager() {
		return entityManager;
	}

	/**
	 * Gets the session.
	 * 
	 * @return the session
	 */
	protected Session getSession() {
		return getEntityManager().unwrap(Session.class);
	}

	/**
	 * Gets the default order specified by the {@link IModel}, as a Hibernate {@link Criteria}-compatible
	 * {@link Order}.
	 * 
	 * @return the default order specified by the {@link IModel}, as a Hibernate {@link Criteria}-compatible
	 *         {@link Order}
	 */
	protected Order[] getDefaultOrderAsCriteria() {
		return defaultOrderAsCriteria;
	}
}
