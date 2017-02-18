package org.demyo.service.impl;

import java.util.List;
import java.util.concurrent.Future;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;

import org.demyo.dao.IModelRepo;
import org.demyo.dao.ISeriesRepo;
import org.demyo.model.Series;
import org.demyo.service.ISeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implements the contract defined by {@link ISeriesService}.
 */
@Service
public class SeriesService extends AbstractModelService<Series> implements ISeriesService {
	@Autowired
	private ISeriesRepo repo;

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
		return entity;
	}

	@Transactional(readOnly = true)
	@Override
	public List<Series> findOtherSeries(long id) {
		return repo.findByIdNot(id);
	}

	@Transactional(rollbackFor = Throwable.class)
	@CacheEvict(cacheNames = "ModelLists", key = "#root.targetClass.simpleName.replaceAll('Service$', '')")
	@Override
	public long save(@NotNull Series model) {
		Series savedModel = repo.saveWithReverseRelations(model);
		return savedModel.getId();
	}

	@Transactional(readOnly = true)
	@Async
	@Override
	public Future<List<Series>> quickSearch(String query, boolean exact) {
		return quickSearch(query, exact, repo);
	}

	@Override
	protected IModelRepo<Series> getRepo() {
		return repo;
	}
}
