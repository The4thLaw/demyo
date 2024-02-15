package org.demyo.service.impl;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.demyo.dao.IAlbumRepo;
import org.demyo.dao.IAuthorRepo;
import org.demyo.dao.IModelRepo;
import org.demyo.model.Author;
import org.demyo.model.beans.AuthorAlbums;
import org.demyo.model.projections.IAuthorAlbum;
import org.demyo.service.IAuthorService;

/**
 * Implements the contract defined by {@link IAuthorService}.
 */
@Service
public class AuthorService extends AbstractModelService<Author> implements IAuthorService {
	@Autowired
	private IAuthorRepo repo;
	@Autowired
	private IAlbumRepo albumRepo;

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
	public CompletableFuture<List<Author>> quickSearch(String query, boolean exact) {
		return quickSearch(query, exact, repo);
	}

	@Override
	@Transactional(readOnly = true)
	public AuthorAlbums getAuthorAlbums(long authorId) {
		AuthorAlbums ret = new AuthorAlbums();

		List<IAuthorAlbum> works = albumRepo.findAlbumsFromAuthor(authorId);
		for (IAuthorAlbum w : works) {
			ret.addWork(w);
		}

		Set<Long> albumIds = ret.getAllAlbumsIds();
		ret.setAlbums(albumRepo.findAllById(albumIds));

		return ret;
	}

	@Override
	protected IModelRepo<Author> getRepo() {
		return repo;
	}
}
