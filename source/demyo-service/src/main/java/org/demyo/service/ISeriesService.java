package org.demyo.service;

import java.util.List;

import org.demyo.model.Series;
import org.demyo.service.impl.IQuickSearchableService;

/**
 * Service for management of {@link Series}.
 */
public interface ISeriesService extends IModelService<Series>, IQuickSearchableService<Series> {

	/**
	 * Finds all Series which are not the given one.
	 * 
	 * @param id The Series ID.
	 * @return The other Series.
	 */
	List<Series> findOtherSeries(long id);

}
