package org.demyo.service.impl;

import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.demyo.common.exception.DemyoErrorCode;
import org.demyo.common.exception.DemyoRuntimeException;
import org.demyo.dao.IAlbumRepo;
import org.demyo.dao.IModelRepo;
import org.demyo.dao.IReaderRepo;
import org.demyo.model.Album;
import org.demyo.model.Reader;
import org.demyo.service.IReaderContext;
import org.demyo.service.IReaderService;
import org.demyo.service.ISeriesService;
import org.demyo.service.ITranslationService;

/**
 * Implements the contract defined by {@link ISeriesService}.
 */
@Service
public class ReaderService extends AbstractModelService<Reader> implements IReaderService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReaderService.class);

	@Autowired
	private IReaderRepo repo;
	@Autowired
	private IReaderContext context;
	@Autowired
	private IAlbumRepo albumRepo;
	@Autowired
	private ITranslationService translationService;

	/**
	 * Default constructor.
	 */
	public ReaderService() {
		super(Reader.class);
	}

	@Override
	public IReaderContext getContext() {
		return context;
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
		long count = repo.count();
		if (count == 0) {
			// Creating the reader here, rather than in the migration script, has some advantages:
			// - It allows generating a reader with a locale-sensitive default name
			// - It acts as a failsafe if the Reader somehow got missing
			// - It deals with imports of Demyo 1.x and 2.0, which didn't have this data
			Reader defaultReader = new Reader();
			defaultReader.setName(translationService.translate("field.Reader.name.default"));
			save(defaultReader);
		} else if (count != 1) {
			return null;
		}
		// TODO: this won't be very efficient if there is a large number of readers.
		// It would be best to find with a limit
		long uniqueId = repo.findAll().iterator().next().getId();
		return getByIdForView(uniqueId);
	}

	@Transactional
	@Override
	public void addFavouriteSeries(long seriesId) {
		LOGGER.debug("Adding favourite series {}", seriesId);

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
	public void removeFavouriteSeries(long seriesId) {
		LOGGER.debug("Removing favourite series {}", seriesId);

		long readerId = context.getCurrentReader().getId();
		// Remove to avoid duplicates
		repo.deleteFavouriteSeries(readerId, seriesId);
		// Clear the context so that it's reloaded for next request
		context.clearCurrentReader();
	}

	@Transactional
	@Override
	public void addFavouriteAlbum(long albumId) {
		LOGGER.debug("Adding favourite album {}", albumId);

		long readerId = context.getCurrentReader().getId();
		// Remove to avoid duplicates
		repo.deleteFavouriteAlbum(readerId, albumId);
		// Add the actual favourite
		repo.insertFavouriteAlbum(readerId, albumId);
		// Clear the context so that it's reloaded for next request
		context.clearCurrentReader();
	}

	@Transactional
	@Override
	public void removeFavouriteAlbum(long albumId) {
		LOGGER.debug("Removing favourite album {}", albumId);

		long readerId = context.getCurrentReader().getId();
		// Remove to avoid duplicates
		repo.deleteFavouriteAlbum(readerId, albumId);
		// Clear the context so that it's reloaded for next request
		context.clearCurrentReader();
	}

	@Transactional
	@Override
	public void addAlbumToReadingList(long albumId) {
		LOGGER.debug("Adding album {} to the reading list", albumId);

		long readerId = context.getCurrentReader().getId();
		addAlbumToReadingList(albumId, readerId);
		context.clearCurrentReader();
	}

	private void addAlbumToReadingList(long albumId, long readerId) {
		repo.deleteFromReadingList(readerId, albumId);
		repo.insertInReadingList(readerId, albumId);
	}

	@Transactional
	@Override
	public void removeAlbumFromReadingList(long albumId) {
		LOGGER.debug("Removing album {} from the reading list", albumId);

		long readerId = context.getCurrentReader().getId();
		repo.deleteFromReadingList(readerId, albumId);
		context.clearCurrentReader();
	}

	@Transactional
	@Override
	public void addSeriesToReadingList(long seriesId) {
		LOGGER.debug("Adding albums from series {} to the reading list", seriesId);

		long readerId = context.getCurrentReader().getId();
		List<Album> albums = albumRepo.findBySeriesIdAndWishlistFalse(seriesId);
		for (Album a : albums) {
			addAlbumToReadingList(a.getId(), readerId);
		}

		context.clearCurrentReader();
	}

	@Transactional(rollbackFor = Throwable.class)
	@CacheEvict(cacheNames = "ModelLists", key = "#root.targetClass.simpleName.replaceAll('Service$', '')")
	@Override
	public void delete(long id) {
		// Override to ensure we don't delete the last reader
		if (getRepo().count() < 2) {
			throw new DemyoRuntimeException(DemyoErrorCode.READER_CANNOT_DELETE_LAST,
					"Cannot delete the last reader in the database");
		}
		super.delete(id);
	}

	@Override
	protected IModelRepo<Reader> getRepo() {
		return repo;
	}

}
