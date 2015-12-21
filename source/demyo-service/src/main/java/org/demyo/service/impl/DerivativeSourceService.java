package org.demyo.service.impl;

import org.demyo.dao.IDerivativeSourceRepo;
import org.demyo.dao.IModelRepo;
import org.demyo.model.DerivativeSource;
import org.demyo.service.IDerivativeSourceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implements the contract defined by {@link IDerivativeSourceService}.
 */
@Service
public class DerivativeSourceService extends AbstractModelService<DerivativeSource> implements
		IDerivativeSourceService {
	@Autowired
	private IDerivativeSourceRepo repo;

	/**
	 * Default constructor.
	 */
	public DerivativeSourceService() {
		super(DerivativeSource.class);
	}

	@Override
	protected IModelRepo<DerivativeSource> getRepo() {
		return repo;
	}
}
