package org.demyo.service.impl;

import org.demyo.dao.IDerivativeSourceDao;
import org.demyo.dao.IModelDao;
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
	private IDerivativeSourceDao dao;

	/**
	 * Default constructor.
	 */
	public DerivativeSourceService() {
		super(DerivativeSource.class);
	}

	@Override
	protected IModelDao<DerivativeSource> getDao() {
		return dao;
	}
}
