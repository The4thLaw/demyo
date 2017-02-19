package org.demyo.dao;

import org.demyo.model.Album;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.querydsl.core.types.Predicate;

/**
 * Implements the contract defined by {@link IAlbumCustomRepo}.
 */
public class IAlbumRepoImpl implements IAlbumCustomRepo {
	@Autowired
	// Inject "self" so that we can use the findAll method
	private IAlbumRepo repo;

	@Override
	public Slice<Album> findAllForIndex(Predicate filter, Pageable pageable) {
		Slice<Album> slice = repo.findAll(filter, pageable);
		// See IPublisherRepoImpl for rationale behind this
		for (Album album : slice) {
			Hibernate.initialize(album.getSeries());
		}
		return slice;
	}
}
