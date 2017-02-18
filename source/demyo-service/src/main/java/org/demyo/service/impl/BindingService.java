package org.demyo.service.impl;

import javax.persistence.EntityNotFoundException;

import org.demyo.dao.IBindingRepo;
import org.demyo.dao.IModelRepo;
import org.demyo.model.Binding;
import org.demyo.service.IBindingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implements the contract defined by {@link IBindingService}.
 */
@Service
public class BindingService extends AbstractModelService<Binding> implements IBindingService {
	@Autowired
	private IBindingRepo repo;

	/**
	 * Default constructor.
	 */
	public BindingService() {
		super(Binding.class);
	}

	@Transactional(readOnly = true)
	@Override
	public Binding getByIdForView(long id) {
		Binding entity = repo.findOneForView(id);
		if (entity == null) {
			throw new EntityNotFoundException("No Binding for ID " + id);
		}
		return entity;
	}

	@Override
	protected IModelRepo<Binding> getRepo() {
		return repo;
	}
}
