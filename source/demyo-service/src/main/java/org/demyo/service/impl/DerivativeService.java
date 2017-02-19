package org.demyo.service.impl;

import org.demyo.dao.IDerivativeRepo;
import org.demyo.dao.IModelRepo;
import org.demyo.model.Derivative;
import org.demyo.service.IDerivativeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.Predicate;

/**
 * Implements the contract defined by {@link IDerivativeService}.
 */
@Service
public class DerivativeService extends AbstractModelService<Derivative> implements IDerivativeService {
	@Autowired
	private IDerivativeRepo repo;

	/**
	 * Default constructor.
	 */
	public DerivativeService() {
		super(Derivative.class);
	}

	@Override
	@Transactional(readOnly = true)
	public Slice<Derivative> findPaginated(int currentPage, Order... orders) {
		return findPaginated(currentPage, null, orders);
	}

	@Override
	@Transactional(readOnly = true)
	public Slice<Derivative> findPaginated(int currentPage, Predicate predicate, Order... orders) {
		return repo.findAllForIndex(predicate, getPageable(currentPage, orders));
	}

	@Override
	protected IModelRepo<Derivative> getRepo() {
		return repo;
	}
}
