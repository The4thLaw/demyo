package org.demyo.service.impl;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.demyo.common.exception.DemyoException;
import org.demyo.dao.IModelRepo;
import org.demyo.dao.IPublisherRepo;
import org.demyo.model.Publisher;
import org.demyo.service.IFilePondModelService;
import org.demyo.service.IPublisherService;

/**
 * Implements the contract defined by {@link IPublisherService}.
 */
@Service
public class PublisherService extends AbstractModelService<Publisher> implements IPublisherService {
	@Autowired
	private IPublisherRepo repo;
	@Autowired
	private IFilePondModelService filePondModelService;

	/**
	 * Default constructor.
	 */
	public PublisherService() {
		super(Publisher.class);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Publisher> findAllForIndex() {
		return repo.findAllForIndex(getDefaultSort());
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
	public CompletableFuture<List<Publisher>> quickSearch(String query, boolean exact) {
		return quickSearch(query, exact, repo);
	}

	@Transactional(rollbackFor = Throwable.class)
	@CacheEvict(cacheNames = "ModelLists", key = "#root.targetClass.simpleName.replaceAll('Service$', '')")
	@Override
	public void recoverFromFilePond(long publisherId, String logoFilePondId) throws DemyoException {
		filePondModelService.recoverFromFilePond(publisherId,
				logoFilePondId, null,
				"special.filepond.Publisher.baseImageName", null,
				Publisher::setLogo, null,
				this, Publisher::getIdentifyingName);
	}

	@Override
	protected IModelRepo<Publisher> getRepo() {
		return repo;
	}
}
