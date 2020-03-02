package org.demyo.model.beans;

import java.util.Set;

/**
 * Defines the various lists of items from a Reader (favourites, reading list...).
 */
public class ReaderLists {
	/** The IDs of the favourite Series. */
	private final Set<Number> favouriteSeries;
	/** The IDs of the favourite Albums. */
	private final Set<Number> favouriteAlbums;
	/** The IDs of the Albums in the reading list. */
	private final Set<Number> readingList;

	/**
	 * Creates the reader lists.
	 * 
	 * @param favouriteSeries The IDs of the favourite Series.
	 * @param favouriteAlbums The IDs of the favourite Albums.
	 * @param readingList The IDs of the Albums in the reading list.
	 */
	public ReaderLists(Set<Number> favouriteSeries, Set<Number> favouriteAlbums, Set<Number> readingList) {
		this.favouriteSeries = favouriteSeries;
		this.favouriteAlbums = favouriteAlbums;
		this.readingList = readingList;
	}

	/**
	 * Gets the IDs of the favourite Series.
	 *
	 * @return the IDs of the favourite Series
	 */
	public Set<Number> getFavouriteSeries() {
		return favouriteSeries;
	}

	/**
	 * Gets the IDs of the favourite Albums.
	 *
	 * @return the IDs of the favourite Albums
	 */
	public Set<Number> getFavouriteAlbums() {
		return favouriteAlbums;
	}

	/**
	 * Gets the IDs of the Albums in the reading list.
	 *
	 * @return the IDs of the Albums in the reading list
	 */
	public Set<Number> getReadingList() {
		return readingList;
	}
}
