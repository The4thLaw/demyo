package org.demyo.service.impl;

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
public class CollectionService extends AbstractModelServiceNG<Collection> implements ICollectionService {
	@Autowired
	private ICollectionRepo repo;

	/**
	 * Default constructor.
	 */
	public CollectionService() {
		super(Collection.class);
	}

	@Override
	protected IModelRepo<Collection> getRepo() {
		return repo;
	}
}
