package org.demyo.service.impl;

import org.demyo.dao.IDerivativeTypeDao;
import org.demyo.dao.IModelDao;
import org.demyo.model.DerivativeType;
import org.demyo.service.IDerivativeTypeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implements the contract defined by {@link IDerivativeTypeService}.
 * 
 * @author $Author: xr $
 * @version $Revision: 1076 $
 */
@Service
public class DerivativeTypeService extends AbstractModelService<DerivativeType> implements IDerivativeTypeService {
	@Autowired
	private IDerivativeTypeDao dao;

	/**
	 * Default constructor.
	 */
	public DerivativeTypeService() {
		super(DerivativeType.class);
	}

	@Override
	protected IModelDao<DerivativeType> getDao() {
		return dao;
	}
}
