package org.demyo.service.impl;

import org.demyo.dao.IDerivativeRepo;
import org.demyo.dao.IModelRepo;
import org.demyo.model.Derivative;
import org.demyo.service.IConfigurationService;
import org.demyo.service.IDerivativeService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implements the contract defined by {@link IDerivativeService}.
 */
@Service
public class DerivativeService extends AbstractModelServiceNG<Derivative> implements IDerivativeService {
	private static final Logger LOGGER = LoggerFactory.getLogger(DerivativeService.class);

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
	protected IModelRepo<Derivative> getRepo() {
		return repo;
	}
}
