package org.demyo.service;

import org.demyo.model.Album;
import org.demyo.model.Series;

/**
 * Service for management of {@link Album}s.
 */
public interface IAlbumService extends IModelService<Album> {

	/**
	 * Finds a template {@link Album} for a given {@link Series}, with the aim of adding a new entry. The retrieved
	 * {@link Album} is the last one from the {@link Series}.
	 * 
	 * @param seriesId The {@link Series} to query.
	 * @return The template {@link Album}.
	 */
	public abstract Album getAlbumTemplateForSeries(long seriesId);

}
