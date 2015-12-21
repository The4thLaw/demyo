package org.demyo.service;

import java.util.List;

import org.demyo.model.Series;

/**
 * Service for management of {@link Series}.
 */
public interface ISeriesService extends IModelService<Series> {

	/**
	 * Finds all Series which are not the given one.
	 * 
	 * @param id The Series ID.
	 * @return The other Series.
	 */
	public List<Series> findOtherSeries(long id);
}
