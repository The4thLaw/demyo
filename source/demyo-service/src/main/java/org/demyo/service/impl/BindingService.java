package org.demyo.service.impl;

import org.demyo.dao.IBindingDao;
import org.demyo.dao.IModelDao;
import org.demyo.model.Binding;
import org.demyo.service.IBindingService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implements the contract defined by {@link IBindingService}.
 * 
 * @author $Author: xr $
 * @version $Revision: 1080 $
 */
@Service
public class BindingService extends AbstractModelService<Binding> implements IBindingService {
	@Autowired
	private IBindingDao dao;

	/**
	 * Default constructor.
	 */
	public BindingService() {
		super(Binding.class);
	}

	@Override
	protected IModelDao<Binding> getDao() {
		return dao;
	}
}
