package org.demyo.dao;

import java.util.List;

import org.demyo.model.Album;
import org.demyo.model.Series;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * This class provides methods to manipulate {@link Album}s.
 */
@Repository
public interface IAlbumRepo extends IModelRepo<Album>, IQuickSearchableRepo<Album> {
	@Override
	@Query("select x from #{#entityName} x where id=?1")
	@EntityGraph("Album.forEdition")
	// TODO: open a feature request to Spring Data to support some genericity in @EntityGraph: automatic context with
	// the class name, or spEL support
	Album findOneForEdition(long id);

	@EntityGraph("Album.forEdition")
	Album findTopBySeriesId(long id, Sort sort);

	/**
	 * Finds the {@link Album}s belonging to a specific {@link Series}.
	 * 
	 * @param seriesId The Series ID
	 * @return The associated Albums
	 */
	List<Album> findBySeriesId(long seriesId);

	@Override
	@Query("select x from #{#entityName} x where title=?1 and wishlist = false order by title")
	List<Album> quickSearchExact(String query);

	@Override
	@Query("select x from #{#entityName} x where title like ?1 and wishlist = false order by title")
	List<Album> quickSearchLike(String query);
}
