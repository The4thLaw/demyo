package org.demyo.dao;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import org.demyo.model.Publisher;

/**
 * Implements the contract defined by {@link IPublisherCustomRepo}.
 */
/* package */class IPublisherRepoImpl implements IPublisherCustomRepo {
	@Autowired
	// Inject "self" so that we can use the findAll method
	private IPublisherRepo repo;

	@Override
	public Slice<Publisher> findAllForIndexOld(Pageable pageable) {
		/* Using an EntityGraph to load all entities messes with Hibernate, as it doesn't know how many results
		 * to fetch (HHH000104). The best solution is to fetch the paged entities without dependencies, and then to
		 * initialise the lazy properties. They should be configured to use batch fetching for limited impacts.
		 * End result: a few queries instead of a single, but fetching only the entities we want.
		 */
		Slice<Publisher> slice = repo.findAll(pageable);
		for (Publisher pub : slice) {
			Hibernate.initialize(pub.getCollections());
		}
		return slice;
	}

}
