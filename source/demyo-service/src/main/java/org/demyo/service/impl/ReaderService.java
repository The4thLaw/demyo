package org.demyo.service.impl;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.demyo.dao.IModelRepo;
import org.demyo.dao.IReaderRepo;
import org.demyo.model.Reader;
import org.demyo.service.IReaderContext;
import org.demyo.service.IReaderService;
import org.demyo.service.ISeriesService;

/**
 * Implements the contract defined by {@link ISeriesService}.
 */
@Service
public class ReaderService extends AbstractModelService<Reader> implements IReaderService {
	@Autowired
	private IReaderRepo repo;
	@Autowired
	private IReaderContext context;

	/**
	 * Default constructor.
	 */
	public ReaderService() {
		super(Reader.class);
	}

	@Override
	public boolean readerExists(long readerId) {
		return repo.exists(readerId);
	}

	@Transactional(readOnly = true)
	@Override
	public Reader getByIdForView(long id) {
		Reader entity = repo.findOneForView(id);
		if (entity == null) {
			throw new EntityNotFoundException("No Reader for ID " + id);
		}
		return entity;
	}

	@Transactional(readOnly = true)
	@Override
	public Reader getUniqueReader() {
		if (repo.count() != 1) {
			return null;
		}
		long uniqueId = repo.findAll().iterator().next().getId();
		return getByIdForView(uniqueId);
	}

	@Transactional
	@Override
	public void addFavouriteSeries(long seriesId) {
		long readerId = context.getCurrentReader().getId();
		// Remove to avoid duplicates
		repo.deleteFavouriteSeries(readerId, seriesId);
		// Add the actual favourite
		repo.insertFavouriteSeries(readerId, seriesId);
		// Clear the context so that it's reloaded for next request
		context.clearCurrentReader();
	}

	@Transactional
	@Override
	public void addFavouriteAlbum(long albumId) {
		long readerId = context.getCurrentReader().getId();
		// Remove to avoid duplicates
		repo.deleteFavouriteAlbum(readerId, albumId);
		// Add the actual favourite
		repo.insertFavouriteAlbum(readerId, albumId);
		// Clear the context so that it's reloaded for next request
		context.clearCurrentReader();
	}

	@Override
	protected IModelRepo<Reader> getRepo() {
		return repo;
	}

}
