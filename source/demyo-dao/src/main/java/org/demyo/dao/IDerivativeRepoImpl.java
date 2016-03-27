package org.demyo.dao;

import org.demyo.model.Derivative;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

/**
 * Implements the contract defined by {@link IDerivativeCustomRepo}.
 */
public class IDerivativeRepoImpl implements IDerivativeCustomRepo {
	@Autowired
	// Inject "self" so that we can use the findAll method
	private IDerivativeRepo repo;

	@Override
	public Slice<Derivative> findAllForIndex(Pageable pageable) {
		// See IPublisherRepoImpl for rationale behind this
		Slice<Derivative> slice = repo.findAll(pageable);
		for (Derivative deriv : slice) {
			Hibernate.initialize(deriv.getArtist());
			Hibernate.initialize(deriv.getImages());
		}
		return slice;
	}
}
