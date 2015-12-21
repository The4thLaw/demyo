package org.demyo.service.impl;

import java.util.List;

import org.demyo.dao.IModelRepo;
import org.demyo.dao.ISeriesRepo;
import org.demyo.model.Series;
import org.demyo.service.ISeriesService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implements the contract defined by {@link ISeriesService}.
 */
@Service
public class SeriesService extends AbstractModelServiceNG<Series> implements ISeriesService {
	@Autowired
	private ISeriesRepo repo;

	/**
	 * Default constructor.
	 */
	public SeriesService() {
		super(Series.class);
	}

	@Transactional(rollbackFor = Throwable.class)
	@Override
	public Series getByIdForView(long id) {
		return repo.findOneForView(id);
	}

	@Override
	public List<Series> findOtherSeries(long id) {
		return repo.findByIdNot(id);
	}

	@Transactional(rollbackFor = Throwable.class)
	@Override
	public long save(Series model) {
		Series savedModel = repo.saveWithReverseRelations(model);
		return savedModel.getId();
	}

	@Override
	protected IModelRepo<Series> getRepo() {
		return repo;
	}
}
