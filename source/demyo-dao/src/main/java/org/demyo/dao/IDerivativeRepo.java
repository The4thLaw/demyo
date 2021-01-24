package org.demyo.dao;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Predicate;

import org.demyo.model.Derivative;

/**
 * This class provides methods to manipulate {@link Derivative}s.
 */
@Repository
public interface IDerivativeRepo extends IModelRepo<Derivative>/*, IDerivativeCustomRepo*/ {
	/**
	 * Returns a {@link List} of entities, fetching the required sub-entities.
	 * 
	 * @return The entities
	 */
	@Override
	@EntityGraph("Derivative.forIndex")
	List<Derivative> findAll();

	/**
	 * Returns a {@link List} of entities respecting the given predicate, fetching the required sub-entities.
	 * 
	 * @param predicate The predicate to filter the entities.
	 * @return The entities
	 */
	@Override
	@EntityGraph("Derivative.forIndex")
	List<Derivative> findAll(Predicate p);

	@Override
	@Query("select x from #{#entityName} x where id=?1")
	@EntityGraph("Derivative.forEdition")
	Derivative findOneForEdition(long id);
}
