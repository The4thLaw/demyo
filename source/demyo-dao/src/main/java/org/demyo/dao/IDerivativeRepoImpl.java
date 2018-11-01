package org.demyo.dao;

import java.util.List;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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
	public List<Derivative> findAllForStickers(Sort sort) {
		List<Derivative> list = repo.findAll(sort);
		// See IPublisherRepoImpl for rationale behind this
		for (Derivative deriv : list) {
			Hibernate.initialize(deriv.getArtist());
		}
		return list;
	}

	@Override
	public Slice<Derivative> findAllForIndex(Predicate filter, Pageable pageable) {
		Slice<Derivative> slice = repo.findAll(filter, pageable);
		// See IPublisherRepoImpl for rationale behind this
		for (Derivative deriv : slice) {
			Hibernate.initialize(deriv.getArtist());
			Hibernate.initialize(deriv.getImages());
		}
		return slice;
	}
}
