package org.demyo.dao;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import com.querydsl.core.types.Predicate;

import org.demyo.model.Derivative;

/**
 * Implements the contract defined by {@link IDerivativeCustomRepo}.
 */
public class IDerivativeRepoImpl implements IDerivativeCustomRepo {
	@Autowired
	// Inject "self" so that we can use the findAll method
	private IDerivativeRepo repo;

	@Override
	public Iterable<Derivative> findAllForIndex(Sort sort) {
		return findAllForIndex(null, sort);
	}

	@Override
	public Iterable<Derivative> findAllForIndex(Predicate p, Sort sort) {
		Iterable<Derivative> list;
		if (p != null) {
			list = repo.findAll(p, sort);
		} else {
			list = repo.findAll(sort);
		}
		// See IPublisherRepoImpl for rationale behind this
		// In this case, having multiple images means that the Derivative is fetched multiple times
		for (Derivative deriv : list) {
			Hibernate.initialize(deriv.getImages());
		}
		return list;
	}
}
