package org.demyo.dao;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.demyo.model.Reader;

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
}
