package org.demyo.service.impl;

import java.util.List;

import org.demyo.dao.IModelDao;
import org.demyo.dao.ITagDao;
import org.demyo.dao.JoinTypeHolder;
import org.demyo.model.Tag;
import org.demyo.service.ITagService;

import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implements the contract defined by {@link ITagService}.
 */
@Service
public class TagService extends AbstractModelService<Tag> implements ITagService {
	@Autowired
	private ITagDao dao;

	/**
	 * Default constructor.
	 */
	public TagService() {
		super(Tag.class);
	}

	@Override
	protected IModelDao<Tag> getDao() {
		return dao;
	}

	@Transactional(readOnly = true)
	@Override
	public List<Tag> findPaginated(int currentPage) {
		JoinTypeHolder holder = new JoinTypeHolder();
		holder.add("taggedAlbums", JoinType.LEFT_OUTER_JOIN);

		// Ignore pagination to make the tag cloud
		return getDao().findAll(null, holder);
	}
}
