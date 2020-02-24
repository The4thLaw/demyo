package org.demyo.dao;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.demyo.model.Album;
import org.demyo.model.Series;
import org.demyo.model.projections.IAuthorAlbum;

/**
 * This class provides methods to manipulate {@link Album}s.
 */
@Repository
public interface IAlbumRepo extends IModelRepo<Album>, IQuickSearchableRepo<Album>, IAlbumCustomRepo {
	/**
	 * Returns a model for the view page.
	 * 
	 * @param id The identifier of the model.
	 * @return The fetched model.
	 */
	@Query("select x from #{#entityName} x where id=?1")
	@EntityGraph("Album.forView")
	Album findOneForView(long id);

	@Override
	@Query("select x from #{#entityName} x where id=?1")
	@EntityGraph("Album.forEdition")
	// TODO [P3]: open a feature request to Spring Data to support some genericity in @EntityGraph: automatic context
	// with the class name, or spEL support
	Album findOneForEdition(long id);

	/**
	 * Finds the first Album for a given Series.
	 * 
	 * @param id The Series ID
	 * @param sort The order to determine the "first"
	 * @return The matching Album
	 */
	@EntityGraph("Album.forEdition")
	Album findTopBySeriesId(long id, Sort sort);

	/**
	 * Finds the {@link Album}s belonging to a specific {@link Series}.
	 * 
	 * @param seriesId The Series ID. Can be <code>null</code> to find Albums without Series
	 * @return The associated Albums
	 */
	List<Album> findBySeriesId(Long seriesId);

	/**
	 * Finds the {@link Album}s belonging to a specific {@link Series} but not in the wishlist.
	 * 
	 * @param seriesId The Series ID.
	 * @return The associated Albums
	 */
	List<Album> findBySeriesIdAndWishlistFalse(Long seriesId);

	@Override
	@Query("select x from #{#entityName} x where title=?1 and wishlist = false order by title")
	List<Album> quickSearchExact(String query);

	@Override
	@Query("select x from #{#entityName} x where title like ?1 and wishlist = false order by title")
	List<Album> quickSearchLike(String query);

	/**
	 * Finds a set of {@link Album}s by their ID. Also fetches the associated {@link Series}.
	 * 
	 * @param albumIds The Album internal IDs
	 * @return The matching Albums
	 */
	@Override
	@EntityGraph("Album.forIndex")
	List<Album> findAll(Iterable<Long> albumIds);

	/**
	 * Finds the albums to which an Author participated, in a structured manner.
	 * 
	 * @param id The Author ID
	 * @return The identifiers of all matching albums, split by author role
	 */
	@Query(value = "select artist_id as author_id, album_id as as_artist, null as as_colorist, null as as_inker,"
			+ " null as as_translator, null as as_writer from albums_artists where artist_id = ?1"
			+ " union all "
			+ "select colorist_id as author_id, null as as_artist, album_id as as_colorist, null as as_inker,"
			+ " null as as_translator, null as as_writer from albums_colorists where colorist_id = ?1"
			+ " union all "
			+ "select inker_id as author_id, null as as_artist, null as as_colorist, album_id as as_inker,"
			+ " null as as_translator, null as as_writer from albums_inkers where inker_id = ?1"
			+ " union all "
			+ "select translator_id as author_id, null as as_artist, null as as_colorist, null as as_inker,"
			+ " album_id as as_translator, null as as_writer from albums_translators where translator_id = ?1"
			+ " union all "
			+ "select writer_id as author_id, null as as_artist, null as as_colorist, null as as_inker,"
			+ " null as as_translator, album_id as as_writer from albums_writers where writer_id = ?1"
			+ "", nativeQuery = true)
	List<IAuthorAlbum> findAlbumsFromAuthor(long id);

	/**
	 * Finds the IDs of the Albums belonging to a Series, ordered logically.
	 * 
	 * @param seriesId The Series ID
	 * @return The Album IDs, if any.
	 */
	@Query("select a.id from Album a where a.series.id=?1 order by cycle asc, number asc, numberSuffix asc, "
			+ "firstEditionDate asc, currentEditionDate asc, title asc")
	List<Long> findAlbumIdsBySeries(long seriesId);
}
