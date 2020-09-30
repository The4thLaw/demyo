package org.demyo.service.impl;

import java.util.List;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.demyo.dao.ICollectionRepo;
import org.demyo.dao.IModelRepo;
import org.demyo.model.Collection;
import org.demyo.service.ICollectionService;

/**
 * Implements the contract defined by {@link ICollectionService}.
 */
@Service
public class CollectionService extends AbstractModelService<Collection> implements ICollectionService {
	@Autowired
	private ICollectionRepo repo;

	/**
	 * Default constructor.
	 */
	public CollectionService() {
		super(Collection.class);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Collection> findByPublisherId(long publisherId) {
		return repo.findByPublisherId(publisherId, getDefaultSort());
	}

	@Async
	@Override
	@Transactional(readOnly = true)
	public Future<List<Collection>> quickSearch(String query, boolean exact) {
		return quickSearch(query, exact, repo);
	}

	@Override
	protected IModelRepo<Collection> getRepo() {
		return repo;
	}
}
