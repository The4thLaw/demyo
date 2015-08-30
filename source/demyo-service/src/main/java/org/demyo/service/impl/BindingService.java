package org.demyo.service.impl;

import org.demyo.dao.IBindingRepo;
import org.demyo.model.Binding;
import org.demyo.service.IBindingService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

/**
 * Implements the contract defined by {@link IBindingService}.
 * 
 * @author $Author: xr $
 * @version $Revision: 1080 $
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

	/*@Override
	protected IModelRepo<Binding> getRepo() {
		return repo;
	}*/

	@Override
	protected JpaRepository<Binding, Long> getRepo() {
		return repo;
	}
}
