package org.demyo.model.beans;

import java.util.Set;

/**
 * Defines the various lists of items from a Reader (favourites, reading list...).
 */
public record ReaderLists(
		/** The IDs of the favourite Series. */
		Set<Number> favouriteSeries,
		/** The IDs of the favourite Albums. */
		Set<Number> favouriteAlbums,
		/** The IDs of the Albums in the reading list. */
		Set<Number> readingList) {

}
