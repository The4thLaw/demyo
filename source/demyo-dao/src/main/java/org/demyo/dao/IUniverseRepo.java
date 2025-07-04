package org.demyo.dao;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.demyo.model.Album;
import org.demyo.model.Universe;

/**
 * This class provides methods to manipulate {@link Universe}s.
 */
@Repository
public interface IUniverseRepo extends IModelRepo<Universe>, IQuickSearchableRepo<Universe> {
	@Override
	@Query("select x from #{#entityName} x where id=?1")
	@EntityGraph("Universe.forEdition")
	Universe findOneForEdition(long id);

	@Query("select x from #{#entityName} x where id=?1")
	@EntityGraph("Universe.forView")
	Universe findOneForView(long id);

	@Query("select a from Album a where a.universe.id=?1 or a.series.universe.id=?1")
	@EntityGraph("Album.forWorks")
	List<Album> findContents(long id);

	@Override
	@Query("select x from #{#entityName} x where name=?1 order by name")
	List<Universe> quickSearchExact(String query);

	@Override
	@Query("select x from #{#entityName} x where name like ?1 order by name")
	List<Universe> quickSearchLike(String query);
}
