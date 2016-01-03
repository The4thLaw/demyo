package org.demyo.dao;

import java.util.List;

import org.demyo.model.Collection;
import org.demyo.model.Publisher;

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
	Collection findOneForEdition(long id);

	/**
	 * Finds the {@link Collection}s belonging to a specific {@link Publisher}.
	 * 
	 * @param publisherId The Publisher ID
	 * @return The associated Collections
	 */
	List<Collection> findByPublisherId(long publisherId);
}
