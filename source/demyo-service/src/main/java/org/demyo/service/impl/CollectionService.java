package org.demyo.service.impl;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.demyo.common.exception.DemyoException;
import org.demyo.dao.ICollectionRepo;
import org.demyo.dao.IModelRepo;
import org.demyo.model.Collection;
import org.demyo.service.ICollectionService;
import org.demyo.service.IFilePondModelService;

/**
 * Implements the contract defined by {@link ICollectionService}.
 */
@Service
public class CollectionService extends AbstractModelService<Collection> implements ICollectionService {
	@Autowired
	private ICollectionRepo repo;
	@Autowired
	private IFilePondModelService filePondModelService;

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
	public CompletableFuture<List<Collection>> quickSearch(String query, boolean exact) {
		return quickSearch(query, exact, repo);
	}

	@Transactional(rollbackFor = Throwable.class)
	@CacheEvict(cacheNames = "ModelLists", key = "#root.targetClass.simpleName.replaceAll('Service$', '')")
	@Override
	public void recoverFromFilePond(long collectionId, String logoFilePondId) throws DemyoException {
		filePondModelService.recoverFromFilePond(collectionId,
				logoFilePondId, null,
				"special.filepond.Collection.baseImageName", null,
				(a, i) -> a.setLogo(i), null,
				this, (a) -> a.getIdentifyingName());
	}

	@Override
	protected IModelRepo<Collection> getRepo() {
		return repo;
	}
}
