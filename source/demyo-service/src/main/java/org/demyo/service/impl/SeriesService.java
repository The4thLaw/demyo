package org.demyo.service.impl;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.demyo.dao.IAlbumRepo;
import org.demyo.dao.IModelRepo;
import org.demyo.dao.ISeriesRepo;
import org.demyo.model.Series;
import org.demyo.service.ISeriesService;

/**
 * Implements the contract defined by {@link ISeriesService}.
 */
@Service
public class SeriesService extends AbstractModelService<Series> implements ISeriesService {
	@Autowired
	private ISeriesRepo repo;
	@Autowired
	private IAlbumRepo albumRepo;

	/**
	 * Default constructor.
	 */
	public SeriesService() {
		super(Series.class);
	}

	@Transactional(readOnly = true)
	@Override
	public Series getByIdForView(long id) {
		Series entity = repo.findOneForView(id);
		if (entity == null) {
			throw new EntityNotFoundException("No Series for ID " + id);
		}
		entity.setAlbumIds(albumRepo.findAlbumIdsBySeries(id));
		return entity;
	}

	@Transactional(readOnly = true)
	@Override
	public List<Series> findOtherSeries(long id) {
		return repo.findByIdNot(id);
	}

	@Transactional(readOnly = true)
	@Async
	@Override
	public CompletableFuture<List<Series>> quickSearch(String query, boolean exact) {
		return quickSearch(query, exact, repo);
	}

	@Override
	protected IModelRepo<Series> getRepo() {
		return repo;
	}
}
