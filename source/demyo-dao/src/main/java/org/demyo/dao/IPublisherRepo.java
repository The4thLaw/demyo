package org.demyo.dao;

import org.demyo.model.Publisher;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * This class provides methods to manipulate {@link Publisher}s.
 */
@Repository
public interface IPublisherRepo extends IModelRepo<Publisher> {
	@Query("select x from #{#entityName} x where id=?1")
	@EntityGraph("Publisher.forView")
	public Publisher findOneForView(long id);

	@Override
	@Query("select x from #{#entityName} x where id=?1")
	@EntityGraph("Publisher.forEdition")
	public Publisher findOneForEdition(long id);

	@Override
	@EntityGraph("Publisher.forIndex")
	Slice<Publisher> findAll(Pageable pageable);
}
