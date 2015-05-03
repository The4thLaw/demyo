package org.demyo.dao.impl;

import org.demyo.dao.IDerivativeTypeDao;
import org.demyo.model.DerivativeType;

import org.springframework.stereotype.Repository;

/**
 * Implements the contract defined by {@link IDerivativeTypeDao}.
 * 
 * @author $Author: xr $
 * @version $Revision: 1076 $
 */
@Repository
public class DerivativeTypeDao extends AbstractModelDao<DerivativeType> implements IDerivativeTypeDao {

	/**
	 * Default constructor.
	 */
	public DerivativeTypeDao() {
		super(DerivativeType.class);
	}
}
