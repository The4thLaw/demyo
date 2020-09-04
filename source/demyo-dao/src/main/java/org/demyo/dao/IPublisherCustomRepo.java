package org.demyo.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import org.demyo.model.Publisher;

/**
 * Custom methods to supplement the default offering of Spring Data.
 */
// TODO: Remove this after switch to Vue
@Deprecated
/* package */interface IPublisherCustomRepo {
	/**
	 * Returns a {@link Slice} of entities meeting the paging restriction provided in the {@code Pageable} object. Will
	 * also fetch the related collections.
	 * 
	 * @param pageable The pagination configuration
	 * @return a page of entities
	 */
	@Deprecated
	Slice<Publisher> findAllForIndexOld(Pageable pageable);
}
