package org.demyo.service.impl;

import java.util.List;
import java.util.Set;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.demyo.common.exception.DemyoErrorCode;
import org.demyo.common.exception.DemyoRuntimeException;
import org.demyo.dao.IAlbumRepo;
import org.demyo.dao.IModelRepo;
import org.demyo.dao.IReaderRepo;
import org.demyo.model.Album;
import org.demyo.model.Reader;
import org.demyo.model.beans.MetaSeries;
import org.demyo.model.beans.ReaderLists;
import org.demyo.model.filters.AlbumFilter;
import org.demyo.service.IAlbumService;
import org.demyo.service.IConfigurationService;
import org.demyo.service.IReaderService;
import org.demyo.service.ISeriesService;
import org.demyo.service.ITranslationService;

import static org.the4thlaw.commons.utils.fluent.FluentUtils.notFoundById;

/**
 * Implements the contract defined by {@link ISeriesService}.
 */
@Service
public class ReaderService extends AbstractModelService<Reader> implements IReaderService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReaderService.class);

	@Autowired
	private IReaderRepo repo;
	@Autowired
	private IAlbumRepo albumRepo;
	@Autowired
	private IAlbumService albumService;
	@Autowired
	private ITranslationService translationService;
	@Autowired
	private IConfigurationService configService;

	/**
	 * Default constructor.
	 */
	public ReaderService() {
		super(Reader.class);
	}

	@Override
	public boolean readerExists(long readerId) {
		return repo.existsById(readerId);
	}

	@Transactional(readOnly = true)
	@Override
	public Reader getByIdForView(long id) {
		Reader entity = repo.findOneForView(id);
		if (entity == null) {
			throw new EntityNotFoundException("No Reader for ID " + id);
		}
		// Initialize the configuration
		entity.initializeConfiguration(configService.getGlobalConfiguration());
		return entity;
	}

	private Reader createDefaultReader() {
		Reader defaultReader = new Reader();
		defaultReader.setName(translationService.translate("field.Reader.name.default"));
		save(defaultReader); // Will create a default configuration

		// Return the created reader rather than reloading it from the database: since the transaction is not
		// yet committed, the configuration entries wouldn't be available
		return defaultReader;
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
			// - It allows creating a default configuration which can evolve over time
			return createDefaultReader();
		} else if (count != 1) {
			return null;
		}
		long uniqueId = repo.findFirstByOrderById().getId();
		return getByIdForView(uniqueId);
	}

	@Transactional(readOnly = false)
	@Cacheable(cacheNames = "ModelLists", key = "#root.targetClass.simpleName.replaceAll('Service$', '')")
	@Override
	public List<Reader> findAll() {
		var readers = super.findAll();
		if (!readers.isEmpty()) {
			return readers;
		}
		// Never allow an empty list of readers, it can for example lead to users not being able to select a reader if
		// theirs was deleted.
		return List.of(createDefaultReader());
	}

	@Transactional
	@Override
	public void addFavouriteSeries(long readerId, long seriesId) {
		LOGGER.debug("Adding favourite series {} to reader {}", seriesId, readerId);

		// Remove to avoid duplicates
		repo.deleteFavouriteSeries(readerId, seriesId);
		// Add the actual favourite
		repo.insertFavouriteSeries(readerId, seriesId);
	}

	@Transactional
	@Override
	public void removeFavouriteSeries(long readerId, long seriesId) {
		LOGGER.debug("Removing favourite series {} from reader {}", seriesId, readerId);
		repo.deleteFavouriteSeries(readerId, seriesId);
	}

	@Transactional
	@Override
	public void addFavouriteAlbum(long readerId, long albumId) {
		LOGGER.debug("Adding favourite album {} to reader {}", albumId, readerId);

		// Remove to avoid duplicates
		repo.deleteFavouriteAlbum(readerId, albumId);
		// Add the actual favourite
		repo.insertFavouriteAlbum(readerId, albumId);
	}

	@Transactional
	@Override
	public void removeFavouriteAlbum(long readerId, long albumId) {
		LOGGER.debug("Removing favourite album {} from reader {}", albumId, readerId);
		repo.deleteFavouriteAlbum(readerId, albumId);
	}

	@Override
	@Transactional
	public void addAlbumToReadingList(long readerId, long albumId) {
		LOGGER.debug("Adding album {} to the reading list of reader {}", albumId, readerId);
		repo.deleteFromReadingList(readerId, albumId);
		repo.insertInReadingList(readerId, albumId);
	}

	@Transactional
	@Override
	public void removeAlbumFromReadingList(long readerId, long albumId) {
		LOGGER.debug("Removing album {} from the reading list of reader {}", albumId, readerId);
		repo.deleteFromReadingList(readerId, albumId);
	}

	@Transactional
	@Override
	public void addSeriesToReadingList(long readerId, long seriesId) {
		LOGGER.debug("Adding albums from series {} to the reading list of {}", seriesId, readerId);

		List<Album> albums = albumRepo.findBySeriesIdAndWishlistFalse(seriesId);
		for (Album a : albums) {
			addAlbumToReadingList(readerId, a.getId());
		}
	}

	@Transactional(rollbackFor = Throwable.class)
	@CacheEvict(cacheNames = "ModelLists", key = "#root.targetClass.simpleName.replaceAll('Service$', '')")
	@Override
	public long save(@NotNull Reader model) {
		Long id = model.getId();
		boolean isNew = id == null;

		if (!isNew) {
			// Preserve the favourites and reading list. Not particularly efficient, but I can't think of a better
			// way while preserving the mapping on the Album side, which is used for QueryDSL queries
			Reader old = repo.findById(id).orElseThrow(notFoundById(Reader.class, id));
			model.setReadingList(old.getReadingList());
			model.setFavouriteSeries(old.getFavouriteSeries());
			model.setFavouriteAlbums(old.getFavouriteAlbums());
		}

		Reader ret = saveAndGetModel(model);

		if (isNew) {
			configService.createDefaultConfiguration(ret);
		} else {
			// Save any configuration update, since this method is used both for the reader and config saves
			configService.save(model.getConfiguration(), ret);
		}

		return ret.getId();
	}

	@Transactional(rollbackFor = Throwable.class)
	@CacheEvict(cacheNames = "ModelLists", key = "#root.targetClass.simpleName.replaceAll('Service$', '')")
	@Override
	public void delete(long id) {
		// Override to ensure we don't delete the last reader
		if (!mayDeleteReader()) {
			throw new DemyoRuntimeException(DemyoErrorCode.READER_CANNOT_DELETE_LAST,
					"Cannot delete the last reader in the database");
		}
		super.delete(id);
	}

	@Transactional(readOnly = true)
	@Override
	public boolean mayDeleteReader() {
		return getRepo().count() >= 2;
	}

	@Override
	public ReaderLists getLists(long readerId) {
		Set<Number> series = repo.getFavouriteSeriesForReader(readerId);
		Set<Number> albums = repo.getFavouriteAlbumsForReader(readerId);
		Set<Number> reading = repo.getReadingListForReader(readerId);
		return new ReaderLists(series, albums, reading);
	}

	@Transactional(readOnly = true)
	@Override
	public Set<Number> getReadingList(long readerId) {
		return repo.getReadingListForReader(readerId);
	}

	@Transactional(readOnly = true)
	@Override
	public Iterable<MetaSeries> getFavouriteAlbums(long readerId) {
		AlbumFilter filter = new AlbumFilter();
		filter.setReaderIdFavourite(readerId);
		return albumService.findAllForIndex(filter);
	}

	@Transactional(readOnly = true)
	@Override
	public Iterable<MetaSeries> getReadingListAlbums(long readerId) {
		AlbumFilter filter = new AlbumFilter();
		filter.setReaderIdReadingList(readerId);
		return albumService.findAllForIndex(filter);
	}

	@Override
	protected IModelRepo<Reader> getRepo() {
		return repo;
	}

}
