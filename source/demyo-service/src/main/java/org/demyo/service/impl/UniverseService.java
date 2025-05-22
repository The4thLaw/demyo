package org.demyo.service.impl;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.demyo.dao.IModelRepo;
import org.demyo.dao.IUniverseRepo;
import org.demyo.model.Universe;
import org.demyo.service.IBindingService;
import org.demyo.service.IUniverseService;

/**
 * Implements the contract defined by {@link IBindingService}.
 */
@Service
public class UniverseService extends AbstractModelService<Universe> implements IUniverseService {
	@Autowired
	private IUniverseRepo repo;

	/**
	 * Default constructor.
	 */
	public UniverseService() {
		super(Universe.class);
	}

	@Override
	@Transactional(readOnly = true)
	public Universe getByIdForView(long id) {
		Universe entity = repo.findOneForView(id);
		if (entity == null) {
			throw new EntityNotFoundException("No Universe for ID " + id);
		}
		return entity;
	}

	@Override
	protected IModelRepo<Universe> getRepo() {
		return repo;
	}
}
