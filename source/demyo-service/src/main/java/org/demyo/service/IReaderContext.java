package org.demyo.service;

import org.demyo.model.Album;
import org.demyo.model.Reader;
import org.demyo.model.Series;

/**
 * Provides methods to access the currently selected {@link Reader}.
 */
public interface IReaderContext {
	/**
	 * Gets the currently selected {@link Reader}.
	 * 
	 * @return The currently selected {@link Reader} (can be <code>null</code>).
	 */
	Reader getCurrentReader();

	/**
	 * Sets the current {@link Reader} in the context of the request.
	 * <p>
	 * This method should be called at least once per request.
	 * </p>
	 * 
	 * @param r The {@link Reader}. Must not be <code>null</code>.
	 */
	void setCurrentReader(Reader r);

	/**
	 * Clears the current {@link Reader}, and any cache related to it.
	 */
	void clearCurrentReader();

	/**
	 * Checks if the provided {@link Series} is a favourite of the currently selected {@link Reader}.
	 * 
	 * @param s The {@link Series} to check.
	 * @return <code>true</code> if the {@link Series} is a favourite.
	 */
	boolean isFavouriteSeries(Series s);

	/**
	 * Checks if the provided {@link Album} is a favourite of the currently selected {@link Reader}.
	 * 
	 * @param a The {@link Album} to check.
	 * @return <code>true</code> if the {@link Album} is a favourite.
	 */
	boolean isFavouriteAlbum(Album a);
}
