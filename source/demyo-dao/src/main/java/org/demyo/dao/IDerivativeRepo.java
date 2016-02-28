package org.demyo.dao;

import org.demyo.model.Derivative;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * This class provides methods to manipulate {@link Derivative}s.
 */
@Repository
public interface IDerivativeRepo extends IModelRepo<Derivative> {
	@Override
	@Query("select x from #{#entityName} x where id=?1")
	@EntityGraph("Derivative.forEdition")
	Derivative findOneForEdition(long id);

	@Override
	@EntityGraph("Derivative.forIndex")
	Slice<Derivative> findAll(Pageable pageable);
}
