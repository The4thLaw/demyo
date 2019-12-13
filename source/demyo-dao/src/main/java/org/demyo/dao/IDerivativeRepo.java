package org.demyo.dao;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.demyo.model.Derivative;

/**
 * This class provides methods to manipulate {@link Derivative}s.
 */
@Repository
public interface IDerivativeRepo extends IModelRepo<Derivative>, IDerivativeCustomRepo {
	/*
	TODO: search the Hibernate docs and figure out why we get multiple results. Distinct doesn't help.
	@Query("select distinct x from #{#entityName} x")
	@EntityGraph("Derivative.forIndex")
	List<Derivative> findAllForIndex(Sort sort);
	/**/

	@Override
	@Query("select x from #{#entityName} x where id=?1")
	@EntityGraph("Derivative.forEdition")
	Derivative findOneForEdition(long id);
}
