package org.demyo.service;

import java.util.Set;

import org.demyo.model.Album;
import org.demyo.model.Reader;
import org.demyo.model.Series;
import org.demyo.model.beans.MetaSeriesNG;
import org.demyo.model.beans.ReaderLists;

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
	 * @deprecated Specify the reader instead
	 */
	@Deprecated
	void addFavouriteSeries(long seriesId);

	/**
	 * Removes a {@link Series} from the favourites of the currently selected {@link Reader}.
	 * 
	 * @param seriesId The {@link Series} ID.
	 * @deprecated Specify the reader instead
	 */
	@Deprecated
	void removeFavouriteSeries(long seriesId);

	/**
	 * Adds an {@link Album} to the favourites of the currently selected {@link Reader}.
	 * 
	 * @param albumId The {@link Album} ID.
	 * @deprecated Specify the reader instead
	 */
	@Deprecated
	void addFavouriteAlbum(long albumId);

	/**
	 * Removes an {@link Album} from the favourites of the currently selected {@link Reader}.
	 * 
	 * @param albumId The {@link Album} ID.
	 * @deprecated Specify the reader instead
	 */
	@Deprecated
	void removeFavouriteAlbum(long albumId);

	/**
	 * Adds a {@link Series} to the favourites of the specified {@link Reader}.
	 * 
	 * @param readerId The {@link Reader} ID.
	 * @param seriesId The {@link Series} ID.
	 */
	void addFavouriteSeries(long readerId, long seriesId);

	/**
	 * Removes a {@link Series} from the favourites of the specified {@link Reader}.
	 * 
	 * @param readerId The {@link Reader} ID.
	 * @param seriesId The {@link Series} ID.
	 */
	void removeFavouriteSeries(long readerId, long seriesId);

	/**
	 * Adds an {@link Album} to the favourites of the specified {@link Reader}.
	 * 
	 * @param readerId The {@link Reader} ID.
	 * @param albumId The {@link Album} ID.
	 */
	void addFavouriteAlbum(long readerId, long albumId);

	/**
	 * Removes an {@link Album} from the favourites of the specified {@link Reader}.
	 * 
	 * @param readerId The {@link Reader} ID.
	 * @param albumId The {@link Album} ID.
	 */
	void removeFavouriteAlbum(long readerId, long albumId);

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
	 * @deprecated Specify the reader instead
	 */
	@Deprecated
	void addAlbumToReadingList(long albumId);

	/**
	 * Removes an {@link Album} from the reading list of the current {@link Reader}.
	 * 
	 * @param albumId The {@link Album} ID.
	 * @deprecated Specify the reader instead
	 */
	@Deprecated
	void removeAlbumFromReadingList(long albumId);

	/**
	 * Adds an entire {@link Series} to the reading list of the current {@link Reader}.
	 * 
	 * @param seriesId The {@link Series} ID.
	 * @deprecated Specify the reader instead
	 */
	@Deprecated
	void addSeriesToReadingList(long seriesId);

	/**
	 * Adds an {@link Album} to the reading list of the specified {@link Reader}.
	 * 
	 * @param readerId The {@link Reader} ID.
	 * @param albumId The {@link Album} ID.
	 */
	void addAlbumToReadingList(long readerId, long albumId);

	/**
	 * Removes an {@link Album} from the reading list of the specified {@link Reader}.
	 * 
	 * @param readerId The {@link Reader} ID.
	 * @param albumId The {@link Album} ID.
	 */
	void removeAlbumFromReadingList(long readerId, long albumId);

	/**
	 * Adds an entire {@link Series} to the reading list of the specified {@link Reader}.
	 * 
	 * @param readerId The {@link Reader} ID.
	 * @param seriesId The {@link Series} ID.
	 */
	void addSeriesToReadingList(long readerId, long seriesId);

	/**
	 * Gets the {@link ReaderLists} for a specific {@link Reader}.
	 * 
	 * @param readerId The {@link Reader} ID.
	 * @return The lists
	 */
	ReaderLists getLists(long readerId);

	/**
	 * Gets the reading list of the specified {@link Reader}.
	 * 
	 * @param readerId The {@link Reader} ID.
	 * @return The reading listS
	 */
	Set<Number> getReadingList(long readerId);

	/**
	 * Finds all {@link Album}s in the {@link Reader}'s favourites, grouped by series, in the suitable order.
	 * <p>
	 * {@link Album}s from favourite {@link Series} are also retrieved.
	 * </p>
	 * 
	 * @return The {@link Album}s grouped by series.
	 */
	Iterable<MetaSeriesNG> getFavouriteAlbums(long modelId);

	/**
	 * Finds all {@link Album}s in the {@link Reader}'s reading list, grouped by series, in the suitable order.
	 * 
	 * @return The {@link Album}s grouped by series.
	 */
	Iterable<MetaSeriesNG> getReadingListAlbums(long modelId);
}
