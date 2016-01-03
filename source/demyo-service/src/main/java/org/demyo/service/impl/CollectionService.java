package org.demyo.service.impl;

import java.util.List;

import org.demyo.dao.ICollectionRepo;
import org.demyo.dao.IModelRepo;
import org.demyo.model.Collection;
import org.demyo.service.ICollectionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
	public List<Collection> findByPublisherId(long publisherId) {
		List<Collection> collections = repo.findByPublisherId(publisherId);
		// Clear the Publisher. It's not needed and would cause lazy loading when used.
		for (Collection c : collections) {
			c.setPublisher(null);
		}
		return collections;
	}

	@Override
	protected IModelRepo<Collection> getRepo() {
		return repo;
	}
}
