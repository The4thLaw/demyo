package org.demyo.service.impl;

import org.demyo.dao.IModelDao;
import org.demyo.dao.ISeriesDao;
import org.demyo.model.Series;
import org.demyo.service.ISeriesService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implements the contract defined by {@link ISeriesService}.
 * 
 * @author $Author: xr $
 * @version $Revision: 1075 $
 */
@Service
public class SeriesService extends AbstractModelService<Series> implements ISeriesService {
	@Autowired
	private ISeriesDao dao;

	/**
	 * Default constructor.
	 */
	public SeriesService() {
		super(Series.class);
	}

	@Override
	protected IModelDao<Series> getDao() {
		return dao;
	}
}
