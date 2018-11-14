package org.demyo.dao;

import java.util.Set;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.demyo.model.Album;
import org.demyo.model.Reader;
import org.demyo.model.Series;

/**
 * This class provides methods to manipulate {@link Reader}s.
 */
@Repository
public interface IReaderRepo extends IModelRepo<Reader> {
	/**
	 * Finds a {@link Reader}, and all his connections to show on a "View" page.
	 * 
	 * @param id The {@link Reader} ID.
	 * @return The found {@link Reader}.
	 */
	@Query("select x from #{#entityName} x where id=?1")
	@EntityGraph("Reader.forView")
	Reader findOneForView(long id);

	/**
	 * Gets the internal identifiers of the Albums in the reading list of the provided Reader.
	 * 
	 * @param readerId The internal identifier of the Reader to query.
	 * @return The IDs. The representation depends on the actual database.
	 */
	@Query(value = "select series_id from readers_favourite_series where reader_id=?1", nativeQuery = true)
	Set<Number> getFavouriteSeriesForReader(long readerId);

	/**
	 * Gets the internal identifiers of the Albums in the reading list of the provided Reader.
	 * 
	 * @param readerId The internal identifier of the Reader to query.
	 * @return The IDs. The representation depends on the actual database.
	 */
	@Query(value = "select album_id from readers_favourite_albums where reader_id=?1", nativeQuery = true)
	Set<Number> getFavouriteAlbumsForReader(long readerId);

	/**
	 * Gets the internal identifiers of the Albums in the reading list of the provided Reader.
	 * 
	 * @param readerId The internal identifier of the Reader to query.
	 * @return The IDs. The representation depends on the actual database.
	 */
	@Query(value = "select album_id from readers_reading_list where reader_id=?1", nativeQuery = true)
	Set<Number> getReadingListForReader(long readerId);

	/**
	 * Removes a favourite {@link Series} for the given {@link Reader}.
	 * 
	 * @param readerId The {@link Reader} ID.
	 * @param seriesId The {@link Series} ID.
	 */
	@Modifying
	@Query(value = "delete from readers_favourite_series where reader_id=?1 and series_id=?2", nativeQuery = true)
	void deleteFavouriteSeries(long readerId, long seriesId);

	/**
	 * Removes a favourite {@link Album} for the given {@link Reader}.
	 * 
	 * @param readerId The {@link Reader} ID.
	 * @param albumId The {@link Album} ID.
	 */
	@Modifying
	@Query(value = "delete from readers_favourite_albums where reader_id=?1 and album_id=?2", nativeQuery = true)
	void deleteFavouriteAlbum(long readerId, long albumId);

	/**
	 * Adds a favourite {@link Series} for the given {@link Reader}.
	 * 
	 * @param readerId The {@link Reader} ID.
	 * @param seriesId The {@link Series} ID.
	 */
	@Modifying
	@Query(value = "insert into readers_favourite_series (reader_id, series_id) VALUES (?1, ?2)", nativeQuery = true)
	void insertFavouriteSeries(long readerId, long seriesId);

	/**
	 * Adds a favourite {@link Album} for the given {@link Reader}.
	 * 
	 * @param readerId The {@link Reader} ID.
	 * @param albumId The {@link Album} ID.
	 */
	@Modifying
	@Query(value = "insert into readers_favourite_albums (reader_id, album_id) VALUES (?1, ?2)", nativeQuery = true)
	void insertFavouriteAlbum(long readerId, long albumId);

	/**
	 * Adds an {@link Album} to the reading list of the given {@link Reader}.
	 * 
	 * @param readerId The {@link Reader} ID.
	 * @param albumId The {@link Album} ID.
	 */
	@Modifying
	@Query(value = "insert into readers_reading_list (reader_id, album_id) VALUES (?1, ?2)", nativeQuery = true)
	void insertInReadingList(long readerId, long albumId);

	/**
	 * Removes an {@link Album} from the reading list of the given {@link Reader}.
	 * 
	 * @param readerId The {@link Reader} ID.
	 * @param albumId The {@link Album} ID.
	 */
	@Modifying
	@Query(value = "delete from readers_reading_list where reader_id=?1 and album_id=?2", nativeQuery = true)
	void deleteFromReadingList(long readerId, long albumId);

	/**
	 * Adds an {@link Album} to the reading list of all {@link Reader}s.
	 * 
	 * @param albumId The {@link Album} ID.
	 */
	@Modifying
	@Query(value = "insert into readers_reading_list (album_id, reader_id) (select ?1, id from readers)", //
			nativeQuery = true)
	void insertInAllReadingLists(long albumId);

	/**
	 * Removes an {@link Album} from the reading list of all {@link Reader}s.
	 * 
	 * @param albumId The {@link Album} ID.
	 */
	@Modifying
	@Query(value = "delete from readers_reading_list where album_id=?1", nativeQuery = true)
	void deleteFromAllReadingLists(long albumId);
}
