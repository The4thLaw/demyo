package org.demyo.service.impl;

import org.demyo.dao.IModelRepo;
import org.demyo.dao.IPublisherRepo;
import org.demyo.model.Publisher;
import org.demyo.service.IPublisherService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implements the contract defined by {@link IPublisherService}.
 */
@Service
public class PublisherService extends AbstractModelServiceNG<Publisher> implements IPublisherService {
	@Autowired
	private IPublisherRepo repo;

	/**
	 * Default constructor.
	 */
	public PublisherService() {
		super(Publisher.class);
	}

	@Transactional(rollbackFor = Throwable.class)
	@Override
	public Publisher getByIdForView(long id) {
		return repo.findOneForView(id);
	}

	@Override
	protected IModelRepo<Publisher> getRepo() {
		return repo;
	}
}
