package org.demyo.dao;

import org.demyo.model.Derivative;

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
	public Derivative findOneForEdition(long id);
}
