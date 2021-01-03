package org.demyo.service.impl;

import java.util.List;
import java.util.concurrent.Future;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.demyo.dao.IModelRepo;
import org.demyo.dao.ITagRepo;
import org.demyo.model.Tag;
import org.demyo.service.ITagService;

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

	@Transactional(readOnly = true)
	@Override
	public Tag getByIdForView(long id) {
		Tag entity = repo.findOneForView(id);
		if (entity == null) {
			throw new EntityNotFoundException("No Tag for ID " + id);
		}
		return entity;
	}

	@Override
	public List<Tag> findAllForIndex() {
		return repo.findAllWithUsageCounts();
	}

	@Async
	@Override
	@Transactional(readOnly = true)
	public Future<List<Tag>> quickSearch(String query, boolean exact) {
		return quickSearch(query, exact, repo);
	}

	@Override
	protected IModelRepo<Tag> getRepo() {
		return repo;
	}
}
