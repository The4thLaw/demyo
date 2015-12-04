package org.demyo.service.impl;

import org.demyo.dao.IModelDao;
import org.demyo.dao.ISeriesDao;
import org.demyo.model.Album;
import org.demyo.model.Series;
import org.demyo.service.ISeriesService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

	@Transactional(rollbackFor = Throwable.class)
	@Override
	public Series getByIdForView(long id) {
		Series series = super.getByIdForView(id);
		// TODO (v2.0.0-alpha3): when switch to Spring data, remove these calls and use graphs. Also remove transaction annotation
		series.getAlbumTags();
		series.getAlbumWriters();
		series.getAlbumArtists();
		series.getAlbumColorists();
		for (Album a : series.getAlbums()) {
			a.getPublisher().getIdentifyingName();
			if (a.getCollection() != null) {
				a.getCollection().getIdentifyingName();
			}
		}
		return series;
	}

	@Override
	protected IModelDao<Series> getDao() {
		return dao;
	}
}
