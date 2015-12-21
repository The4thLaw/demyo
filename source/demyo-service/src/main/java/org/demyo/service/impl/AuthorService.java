package org.demyo.service.impl;

import org.demyo.dao.IAuthorRepo;
import org.demyo.dao.IModelRepo;
import org.demyo.model.Author;
import org.demyo.service.IAuthorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implements the contract defined by {@link IAuthorService}.
 */
@Service
public class AuthorService extends AbstractModelService<Author> implements IAuthorService {
	@Autowired
	private IAuthorRepo repo;

	/**
	 * Default constructor.
	 */
	public AuthorService() {
		super(Author.class);
	}

	@Transactional(rollbackFor = Throwable.class)
	@Override
	public Author getByIdForView(long id) {
		return repo.findOneForView(id);
	}

	@Override
	protected IModelRepo<Author> getRepo() {
		return repo;
	}
}
