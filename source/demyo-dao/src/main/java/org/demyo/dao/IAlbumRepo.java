package org.demyo.dao;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.demyo.model.Album;
import org.demyo.model.Series;

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
}
