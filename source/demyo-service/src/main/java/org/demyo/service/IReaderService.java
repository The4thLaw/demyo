package org.demyo.service;

import org.demyo.model.Album;
import org.demyo.model.Reader;
import org.demyo.model.Series;

/**
 * Service for management of {@link Reader}s.
 */
public interface IReaderService extends IModelService<Reader> {
	/**
	 * Checks if the provided Reader exists.
	 * 
	 * @param readerId The Reader identifier to check.
	 * @return <code>true</code> iif the reader exists.
	 */
	boolean readerExists(long readerId);

	/**
	 * Gets the only Reader from the database.If there is not exactly one Reader, return <code>null</code>.
	 * 
	 * @return The unique Reader, or <code>null</code>.
	 */
	Reader getUniqueReader();

	/**
	 * Adds a {@link Series} to the favourites of the currently selected {@link Reader}.
	 * 
	 * @param seriesId The {@link Series} ID.
	 */
	void addFavouriteSeries(long seriesId);

	/**
	 * Removes a {@link Series} from the favourites of the currently selected {@link Reader}.
	 * 
	 * @param seriesId The {@link Series} ID.
	 */
	void removeFavouriteSeries(long seriesId);

	/**
	 * Adds an {@link Album} to the favourites of the currently selected {@link Reader}.
	 * 
	 * @param albumId The {@link Album} ID.
	 */
	void addFavouriteAlbum(long albumId);

	/**
	 * Removes an {@link Album} from the favourites of the currently selected {@link Reader}.
	 * 
	 * @param albumId The {@link Album} ID.
	 */
	void removeFavouriteAlbum(long albumId);

	/**
	 * Gets the current reader context.
	 * 
	 * @return The {@link IReaderContext context}.
	 */
	IReaderContext getContext();

	/**
	 * Adds an {@link Album} to the reading list of the current {@link Reader}.
	 * 
	 * @param albumId The {@link Album} ID.
	 */
	void addAlbumToReadingList(long albumId);

	/**
	 * Removes an {@link Album} from the reading list of the current {@link Reader}.
	 * 
	 * @param albumId The {@link Album} ID.
	 */
	void removeAlbumFromReadingList(long albumId);
}
