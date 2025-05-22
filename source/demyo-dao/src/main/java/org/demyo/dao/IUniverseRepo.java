package org.demyo.dao;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.demyo.model.Universe;

/**
 * This class provides methods to manipulate {@link Universe}s.
 */
@Repository
public interface IUniverseRepo extends IModelRepo<Universe> {
	@Override
	@Query("select x from #{#entityName} x where id=?1")
	@EntityGraph("Universe.forEdition")
	Universe findOneForEdition(long id);

	@Query("select x from #{#entityName} x where id=?1")
	@EntityGraph("Universe.forView")
	Universe findOneForView(long id);
}
