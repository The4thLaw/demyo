package org.demyo.dao;

import org.demyo.model.Publisher;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

/* package */interface IPublisherCustomRepo {
	/**
	 * Returns a {@link Slice} of entities meeting the paging restriction provided in the {@code Pageable} object.
	 * Will also fetch the related collections.
	 * 
	 * @param pageable The pagination configuration
	 * @return a page of entities
	 */
	Slice<Publisher> findAllForIndex(Pageable pageable);
}
