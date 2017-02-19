package org.demyo.dao;

import org.demyo.model.Album;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.querydsl.core.types.Predicate;

/**
 * Custom methods to supplement the default offering of Spring Data.
 */
public interface IAlbumCustomRepo {
	/**
	 * Returns a {@link Slice} of entities meeting the paging restriction provided in the {@code Pageable} object. Will
	 * also fetch the related artists and images.
	 * 
	 * @param pageable The pagination configuration
	 * @param filter A potential filter for the query
	 * @return a page of entities
	 */
	Slice<Album> findAllForIndex(Predicate filter, Pageable pageable);
}
