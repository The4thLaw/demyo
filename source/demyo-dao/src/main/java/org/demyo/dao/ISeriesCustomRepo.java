package org.demyo.dao;

import org.demyo.model.Series;

/**
 * Custom methods to supplement the default offering of Spring Data.
 */
interface ISeriesCustomRepo {

	/**
	 * Saves the provided Series as well as the reverse links from the related series to this one.
	 * 
	 * @param s The Series to save.
	 * @return The saved Series.
	 */
	public Series saveWithReverseRelations(Series s);

}
