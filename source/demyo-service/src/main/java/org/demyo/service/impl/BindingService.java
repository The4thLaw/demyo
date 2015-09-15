package org.demyo.service.impl;

import org.demyo.dao.IBindingRepo;
import org.demyo.dao.IModelRepo;
import org.demyo.model.Binding;
import org.demyo.service.IBindingService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implements the contract defined by {@link IBindingService}.
 */
@Service
public class BindingService extends AbstractModelServiceNG<Binding> implements IBindingService {
	@Autowired
	private IBindingRepo repo;

	/**
	 * Default constructor.
	 */
	public BindingService() {
		super(Binding.class);
	}

	@Override
	protected IModelRepo<Binding> getRepo() {
		return repo;
	}
}
