package org.demyo.service;

import java.util.List;

import org.demyo.model.Album;
import org.demyo.model.Series;
import org.demyo.service.impl.IQuickSearchableService;

/**
 * Service for management of {@link Album}s.
 */
public interface IAlbumService extends IModelService<Album>, IQuickSearchableService<Album> {

	/**
	 * Finds a template {@link Album} for a given {@link Series}, with the aim of adding a new entry. The retrieved
	 * {@link Album} is the last one from the {@link Series}.
	 * 
	 * @param seriesId The {@link Series} to query.
	 * @return The template {@link Album}.
	 */
	Album getAlbumTemplateForSeries(long seriesId);

	/**
	 * Finds the {@link Album}s belonging to a specific {@link Series}.
	 * 
	 * @param seriesId The Series ID. Can be <code>null</code> to find Albums without Series
	 * @return The associated Albums
	 */
	List<Album> findBySeriesId(Long seriesId);

}
