package org.demyo.service.impl;

import java.util.List;
import java.util.concurrent.Future;

import org.demyo.dao.IModelRepo;
import org.demyo.dao.ITagRepo;
import org.demyo.model.Tag;
import org.demyo.service.ITagService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implements the contract defined by {@link ITagService}.
 */
@Service
public class TagService extends AbstractModelService<Tag> implements ITagService {
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

	@Async
	@Override
	public Future<List<Tag>> quickSearch(String query, boolean exact) {
		return quickSearch(query, exact, repo);
	}

	@Override
	protected IModelRepo<Tag> getRepo() {
		return repo;
	}

}
