package org.demyo.service.impl;

import org.demyo.dao.IModelRepo;
import org.demyo.dao.ITagRepo;
import org.demyo.model.Tag;
import org.demyo.service.ITagService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implements the contract defined by {@link ITagService}.
 */
@Service
public class TagService extends AbstractModelServiceNG<Tag> implements ITagService {
	@Autowired
	private ITagRepo repo;

	/**
	 * Default constructor.
	 */
	public TagService() {
		super(Tag.class);
	}

	@Transactional(rollbackFor = Throwable.class)
	@Override
	public Tag getByIdForView(long id) {
		return repo.findOneForView(id);
	}

	@Transactional(readOnly = true)
	@Override
	public Slice<Tag> findPaginated(int currentPage, Order... orders) {
		return new SliceImpl<>(repo.findAllWithUsageCounts());
	}

	@Override
	protected IModelRepo<Tag> getRepo() {
		return repo;
	}

}
