package org.demyo.dao;

import org.demyo.model.Collection;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * This class provides methods to manipulate {@link Collection}s.
 */
@Repository
public interface ICollectionRepo extends IModelRepo<Collection> {
	@Override
	@Query("select x from #{#entityName} x where id=?1")
	@EntityGraph("Collection.forEdition")
	public Collection findOneForEdition(long id);
}
