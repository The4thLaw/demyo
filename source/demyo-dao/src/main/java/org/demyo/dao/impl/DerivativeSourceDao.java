package org.demyo.dao.impl;

import org.demyo.dao.IDerivativeSourceDao;
import org.demyo.model.DerivativeSource;

import org.springframework.stereotype.Repository;

/**
 * Implements the contract defined by {@link IDerivativeSourceDao}.
 */
@Repository
public class DerivativeSourceDao extends AbstractModelDao<DerivativeSource> implements IDerivativeSourceDao {

	/**
	 * Default constructor.
	 */
	public DerivativeSourceDao() {
		super(DerivativeSource.class);
	}
}
