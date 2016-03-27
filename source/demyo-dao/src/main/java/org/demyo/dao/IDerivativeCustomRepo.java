package org.demyo.dao;

import org.demyo.model.Derivative;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

/**
 * Custom methods to supplement the default offering of Spring Data.
 */
public interface IDerivativeCustomRepo {
	/**
	 * Returns a {@link Slice} of entities meeting the paging restriction provided in the {@code Pageable} object.
	 * Will also fetch the related artists and images.
	 * 
	 * @param pageable The pagination configuration
	 * @return a page of entities
	 */
	Slice<Derivative> findAllForIndex(Pageable pageable);
}
