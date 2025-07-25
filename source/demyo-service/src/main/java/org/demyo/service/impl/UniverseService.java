package org.demyo.service.impl;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
