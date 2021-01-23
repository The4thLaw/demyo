package org.demyo.dao;

import org.springframework.data.domain.Sort;

import com.querydsl.core.types.Predicate;

import org.demyo.model.Derivative;

/**
 * Custom methods to supplement the default offering of Spring Data.
 */
public interface IDerivativeCustomRepo {
	/**
	 * Returns an {@link Iterable} of entities meeting the requested order. Will also fetch the related images.
	 * 
	 * @param sort The order to respect
	 * @return The entities
	 */
	Iterable<Derivative> findAllForIndex(Sort sort);

	/**
	 * Returns an {@link Iterable} of entities meeting the requested order and predicate. Will also fetch the related
	 * images.
	 * 
	 * @param predicate The predicate to filter the entities.
	 * @param sort The order to respect
	 * @return The entities
	 */
	Iterable<Derivative> findAllForIndex(Predicate p, Sort sort);
}
