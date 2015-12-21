package org.demyo.service.impl;

import org.demyo.dao.IDerivativeTypeRepo;
import org.demyo.dao.IModelRepo;
import org.demyo.model.DerivativeType;
import org.demyo.service.IDerivativeTypeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implements the contract defined by {@link IDerivativeTypeService}.
 */
@Service
public class DerivativeTypeService extends AbstractModelService<DerivativeType> implements
		IDerivativeTypeService {
	@Autowired
	private IDerivativeTypeRepo repo;

	/**
	 * Default constructor.
	 */
	public DerivativeTypeService() {
		super(DerivativeType.class);
	}

	@Override
	protected IModelRepo<DerivativeType> getRepo() {
		return repo;
	}
}
