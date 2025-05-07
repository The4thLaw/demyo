package org.demyo.dao;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Predicate;

import org.demyo.model.Album;
import org.demyo.model.BookType;
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
	@EntityGraph("Album.forIndex")
	List<Album> quickSearchExact(String query);

	@Override
	@Query("select x from #{#entityName} x where title like ?1 and wishlist = false order by title")
	@EntityGraph("Album.forIndex")
	List<Album> quickSearchLike(String query);

	/**
	 * Finds a set of {@link Album}s by their ID. Also fetches the associated {@link Series} and {@link BookType}.
	 *
	 * @param albumIds The Album internal IDs
	 * @return The matching Albums
	 */
	@Override
	@EntityGraph("Album.forWorks")
	List<Album> findAllById(Iterable<Long> albumIds);

	/**
	 * Returns a {@link List} of entities. Will also fetch the related {@link Series}.
	 *
	 * @return The entities
	 */
	@Override
	@EntityGraph("Album.forIndex")
	List<Album> findAll();

	/**
	 * Returns a {@link List} of entities meeting the requested predicate. Will also fetch the related {@link Series}.
	 *
	 * @param p The predicate to filter the entities.
	 * @return The entities
	 */
	@Override
	@EntityGraph("Album.forIndex")
	List<Album> findAll(Predicate p);

	/**
	 * Finds the albums to which an Author participated, in a structured manner.
	 *
	 * @param id The Author ID
	 * @return The identifiers of all matching albums, split by author role
	 */
	@Query(value = "select artist_id as authorId, album_id as asArtist, null as asColorist, null as asInker,"
			+ " null as asTranslator, null as asWriter from albums_artists where artist_id = ?1"
			+ " union all "
			+ "select colorist_id as authorId, null as asArtist, album_id as asColorist, null as asInker,"
			+ " null as asTranslator, null as asWriter from albums_colorists where colorist_id = ?1"
			+ " union all "
			+ "select inker_id as authorId, null as asArtist, null as asColorist, album_id as asInker,"
			+ " null as asTranslator, null as asWriter from albums_inkers where inker_id = ?1"
			+ " union all "
			+ "select translator_id as authorId, null as asArtist, null as asColorist, null as asInker,"
			+ " album_id as asTranslator, null as asWriter from albums_translators where translator_id = ?1"
			+ " union all "
			+ "select writer_id as authorId, null as asArtist, null as asColorist, null as asInker,"
			+ " null as asTranslator, album_id as asWriter from albums_writers where writer_id = ?1"
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

	/**
	 * Counts the number of Albums that feature the provided tag.
	 *
	 * @param tagId The Tag internal ID
	 * @return The album count
	 */
	// Way more efficient than any JPA query we could make
	@Query(value = "select count(*) from albums_tags where tag_id = ?1", nativeQuery = true)
	int countAlbumsByTag(long tagId);

	/**
	 * Counts how many Albums use the given Binding.
	 *
	 * @param bindingId The internal ID of the Binding
	 * @return the count
	 */
	@Query("select count(*) from Album a where a.binding.id = ?1")
	int countAlbumsByBinding(long typeId);
}
