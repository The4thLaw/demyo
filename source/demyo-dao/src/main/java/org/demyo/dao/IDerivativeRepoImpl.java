package org.demyo.dao;

import org.demyo.model.Derivative;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.querydsl.core.types.Predicate;

/**
 * Implements the contract defined by {@link IDerivativeCustomRepo}.
 */
public class IDerivativeRepoImpl implements IDerivativeCustomRepo {
	@Autowired
	// Inject "self" so that we can use the findAll method
	private IDerivativeRepo repo;

	@Override
	public Slice<Derivative> findAllForIndex(Pageable pageable, Predicate filter) {
		Slice<Derivative> slice = repo.findAll(filter, pageable);
		// See IPublisherRepoImpl for rationale behind this
		for (Derivative deriv : slice) {
			Hibernate.initialize(deriv.getArtist());
			Hibernate.initialize(deriv.getImages());
		}
		return slice;
	}
}
