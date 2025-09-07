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
import org.demyo.dao.IUniverseRepo;
import org.demyo.model.Album;
import org.demyo.model.Universe;
import org.demyo.model.util.AlbumAndSeriesComparator;
import org.demyo.service.IBindingService;
import org.demyo.service.IUniverseService;

/**
 * Implements the contract defined by {@link IBindingService}.
 */
@Service
public class UniverseService extends AbstractModelService<Universe> implements IUniverseService {
	@Autowired
	private IUniverseRepo repo;
	@Autowired
	private FilePondModelService filePondModelService;

	/**
	 * Default constructor.
	 */
	public UniverseService() {
		super(Universe.class);
	}

	@Override
	@Transactional(readOnly = true)
	public Universe getByIdForView(long id) {
		Universe entity = repo.findOneForView(id);
		if (entity == null) {
			throw new EntityNotFoundException("No Universe for ID " + id);
		}
		return entity;
	}

	@Transactional(readOnly = true)
	@Async
	@Override
	public CompletableFuture<List<Universe>> quickSearch(String query, boolean exact) {
		return quickSearch(query, exact, repo);
	}

	@Override
	protected IModelRepo<Universe> getRepo() {
		return repo;
	}

	@Transactional(readOnly = true)
	@Override
	public List<Album> getContents(long id) {
		List<Album> contents = repo.findContents(id);
		contents.sort(new AlbumAndSeriesComparator());
		return contents;
	}

	@Transactional(rollbackFor = Throwable.class)
	@CacheEvict(cacheNames = "ModelLists", key = "#root.targetClass.simpleName.replaceAll('Service$', '')")
	@Override
	public void recoverFromFilePond(long universeId, String logoFilePondId, String[] otherFilePondIds)
			throws DemyoException {
		filePondModelService.recoverFromFilePond(universeId,
				logoFilePondId, otherFilePondIds,
				"special.filepond.Universe.baseCoverName", "special.filepond.Album.baseImageName",
				Universe::setLogo, (u, li) -> u.getImages().addAll(li),
				this, Universe::getIdentifyingName);
	}
}
