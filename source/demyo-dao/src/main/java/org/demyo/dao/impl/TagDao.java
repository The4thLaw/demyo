package org.demyo.dao.impl;

import java.util.List;

import org.demyo.dao.ITagDao;
import org.demyo.model.Tag;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.sql.JoinType;
import org.springframework.stereotype.Repository;

/**
 * Implements the contract defined by {@link ITagDao}.
 */
@Repository
public class TagDao extends AbstractModelDao<Tag> implements ITagDao {

	/**
	 * Default constructor.
	 */
	public TagDao() {
		super(Tag.class);
	}

	public List<Tag> findAllTagsWithUsageCount() {
		Criteria criteria = getSession().createCriteria(Tag.class);
		criteria.createAlias("taggedAlbums", "album", JoinType.LEFT_OUTER_JOIN);

		for (Order order : getDefaultOrderAsCriteria()) {
			criteria.addOrder(order);
		}

		// criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		@SuppressWarnings("unchecked")
		List<Tag> result = criteria.list();

		return result;
	}
}
