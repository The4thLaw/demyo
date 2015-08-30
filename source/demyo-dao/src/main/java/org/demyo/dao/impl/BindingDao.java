package org.demyo.dao.impl;

import org.demyo.dao.IBindingDao;
import org.demyo.model.Binding;

import org.springframework.stereotype.Repository;

/**
 * Implements the contract defined by {@link IBindingDao}.
 * 
 * @author $Author: xr $
 * @version $Revision: 1080 $
 */
@Repository
@Deprecated
public class BindingDao extends AbstractModelDao<Binding> implements IBindingDao {

	/**
	 * Default constructor.
	 */
	public BindingDao() {
		super(Binding.class);
	}
}
