package org.demyo.dao;

import org.demyo.model.Album;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * This class provides methods to manipulate {@link Album}s.
 */
@Repository
public interface IAlbumRepo extends IModelRepo<Album> {
	@Override
	@Query("select x from #{#entityName} x where id=?1")
	@EntityGraph("Album.forEdition")
	// TODO: open a feature request to Spring Data to support some genericity in @EntityGraph: automatic context with
	// the class name, or spEL support
	public Album findOneForEdition(long id);

	@EntityGraph("Album.forEdition")
	public Album findTopBySeriesId(long id, Sort sort);
}
