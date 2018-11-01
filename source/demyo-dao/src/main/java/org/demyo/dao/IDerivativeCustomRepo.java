package org.demyo.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

import com.querydsl.core.types.Predicate;

import org.demyo.model.Derivative;

/**
 * Custom methods to supplement the default offering of Spring Data.
 */
public interface IDerivativeCustomRepo {
	/**
	 * Returns a {@link List} of entities meeting request order. Will also fetch the related artists.
	 * 
	 * @param sort The order to respect
	 * @return The entities
	 */
	List<Derivative> findAllForStickers(Sort sort);

	/**
	 * Returns a {@link Slice} of entities meeting the paging restriction provided in the {@code Pageable} object. Will
	 * also fetch the related artists and images.
	 * 
	 * @param pageable The pagination configuration
	 * @param filter A potential filter for the query
	 * @return A page of entities
	 */
	Slice<Derivative> findAllForIndex(Predicate filter, Pageable pageable);
}
