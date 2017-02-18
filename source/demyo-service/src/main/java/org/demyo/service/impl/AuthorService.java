package org.demyo.service.impl;

import java.util.List;
import java.util.concurrent.Future;

import javax.persistence.EntityNotFoundException;

import org.demyo.dao.IAuthorRepo;
import org.demyo.dao.IModelRepo;
import org.demyo.model.Author;
import org.demyo.service.IAuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
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

	@Override
	@Transactional(readOnly = true)
	public Author getByIdForView(long id) {
		Author entity = repo.findOneForView(id);
		if (entity == null) {
			throw new EntityNotFoundException("No Author for ID " + id);
		}
		return entity;
	}

	@Async
	@Override
	@Transactional(readOnly = true)
	public Future<List<Author>> quickSearch(String query, boolean exact) {
		return quickSearch(query, exact, repo);
	}

	@Override
	protected IModelRepo<Author> getRepo() {
		return repo;
	}
}
