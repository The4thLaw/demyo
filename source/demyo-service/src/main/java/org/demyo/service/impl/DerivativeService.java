package org.demyo.service.impl;

import org.demyo.dao.IDerivativeRepo;
import org.demyo.dao.IModelRepo;
import org.demyo.model.Derivative;
import org.demyo.service.IConfigurationService;
import org.demyo.service.IDerivativeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implements the contract defined by {@link IDerivativeService}.
 */
@Service
public class DerivativeService extends AbstractModelService<Derivative> implements IDerivativeService {
	@Autowired
	private IDerivativeRepo repo;
	@Autowired
	private IConfigurationService configurationService;

	/**
	 * Default constructor.
	 */
	public DerivativeService() {
		super(Derivative.class);
	}

	@Override
	@Transactional(readOnly = true)
	public Slice<Derivative> findPaginated(int currentPage, Order... orders) {
		return repo.findAllForIndex(getPageable(currentPage, orders));
	}

	@Override
	protected IModelRepo<Derivative> getRepo() {
		return repo;
	}
}
