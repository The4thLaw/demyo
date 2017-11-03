package org.demyo.dao;

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
}
