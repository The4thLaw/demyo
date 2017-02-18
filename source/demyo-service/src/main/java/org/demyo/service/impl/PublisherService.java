package org.demyo.service.impl;

import java.util.List;
import java.util.concurrent.Future;

import javax.persistence.EntityNotFoundException;

import org.demyo.dao.IModelRepo;
import org.demyo.dao.IPublisherRepo;
import org.demyo.model.Publisher;
import org.demyo.service.IPublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implements the contract defined by {@link IPublisherService}.
 */
@Service
public class PublisherService extends AbstractModelService<Publisher> implements IPublisherService {
	@Autowired
	private IPublisherRepo repo;

	/**
	 * Default constructor.
	 */
	public PublisherService() {
		super(Publisher.class);
	}

	@Override
	@Transactional(readOnly = true)
	public Slice<Publisher> findPaginated(int currentPage, Order... orders) {
		return repo.findAllForIndex(getPageable(currentPage, orders));
	}

	@Override
	@Transactional(readOnly = true)
	public Publisher getByIdForView(long id) {
		Publisher entity = repo.findOneForView(id);
		if (entity == null) {
			throw new EntityNotFoundException("No Publisher for ID " + id);
		}
		return entity;
	}

	@Async
	@Override
	public Future<List<Publisher>> quickSearch(String query, boolean exact) {
		return quickSearch(query, exact, repo);
	}

	@Override
	protected IModelRepo<Publisher> getRepo() {
		return repo;
	}
}
