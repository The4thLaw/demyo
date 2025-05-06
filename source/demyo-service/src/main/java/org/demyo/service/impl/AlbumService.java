package org.demyo.service.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.demyo.common.exception.DemyoErrorCode;
import org.demyo.common.exception.DemyoException;
import org.demyo.common.exception.DemyoRuntimeException;
import org.demyo.dao.IAlbumRepo;
import org.demyo.dao.IModelRepo;
import org.demyo.dao.IReaderRepo;
import org.demyo.dao.ISeriesRepo;
import org.demyo.model.Album;
import org.demyo.model.Image;
import org.demyo.model.Series;
import org.demyo.model.beans.MetaSeries;
import org.demyo.model.filters.AlbumFilter;
import org.demyo.model.util.AlbumComparator;
import org.demyo.service.IAlbumService;
import org.demyo.service.IImageService;
import org.demyo.service.ITranslationService;

import static org.the4thlaw.commons.utils.fluent.FluentUtils.notFoundById;

/**
 * Implements the contract defined by {@link IAlbumService}.
 */
@Service
public class AlbumService extends AbstractModelService<Album> implements IAlbumService {
	private static final Logger LOGGER = LoggerFactory.getLogger(AlbumService.class);

	@Autowired
	private IAlbumRepo repo;
	@Autowired
	private ISeriesRepo seriesRepo;
	@Autowired
	private IReaderRepo readerRepo;
	@Autowired
	private IBookTypeService bookTypeService;
	@Autowired
	private IImageService imageService;
	@Autowired
	private ITranslationService translationService;

	/**
	 * Default constructor.
	 */
	public AlbumService() {
		super(Album.class);
	}

	@Transactional(readOnly = true)
	@Override
	public Album getByIdForView(long id) {
		Album entity = repo.findOneForView(id);
		if (entity == null) {
			throw new EntityNotFoundException("No Album for ID " + id);
		}
		return entity;
	}

	@Override
	@Transactional(readOnly = true)
	public Collection<MetaSeries> findAllForIndex() {
		return findAllForIndex(null);
	}

	@Override
	@Transactional(readOnly = true)
	public Collection<MetaSeries> findAllForIndex(AlbumFilter filter) {
		List<Album> albums;
		if (filter == null) {
			albums = repo.findAll();
		} else {
			albums = repo.findAll(filter.toPredicate());
		}

		HashMap<Long, MetaSeries> seriesMap = new HashMap<>();
		SortedSet<MetaSeries> sortedMetas = new TreeSet<>();

		for (Album a : albums) {
			if (a.getSeries() == null) {
				MetaSeries meta = new MetaSeries(a);
				sortedMetas.add(meta);
			} else {
				MetaSeries meta = seriesMap.get(a.getSeries().getId());
				if (meta != null) {
					meta.addAlbum(a);
				} else {
					meta = new MetaSeries(a);
					seriesMap.put(a.getSeries().getId(), meta);
					sortedMetas.add(meta);
				}
			}
		}

		return sortedMetas;
	}

	@Override
	@Transactional(readOnly = true)
	public Album getAlbumTemplateForSeries(long seriesId) {
		Sort sort = Sort.by(Direction.DESC, "cycle", "number", "numberSuffix", "firstEditionDate",
				"currentEditionDate", "title");
		Album last = repo.findTopBySeriesId(seriesId, sort);

		Album template = new Album();

		if (last != null) {
			template.setBookType(last.getBookType());
			template.setArtists(last.getArtists());
			template.setBinding(last.getBinding());
			template.setCollection(last.getCollection());
			template.setColorists(last.getColorists());
			template.setHeight(last.getHeight());
			template.setInkers(last.getInkers());
			template.setPages(last.getPages());
			template.setPublisher(last.getPublisher());
			template.setSeries(last.getSeries());
			template.setTags(last.getTags());
			template.setTranslators(last.getTranslators());
			template.setWidth(last.getWidth());
			template.setWriters(last.getWriters());
		} else {
			template.setSeries(seriesRepo.findById(seriesId).orElseThrow(notFoundById(Series.class, seriesId)));
			// Force the proxy to initialize
			template.getSeries().getName();
		}

		return template;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Album> findBySeriesId(Long seriesId) {
		List<Album> albums = repo.findBySeriesId(seriesId);
		Collections.sort(albums, new AlbumComparator());
		return albums;
	}

	@Async
	@Override
	@Transactional(readOnly = true)
	public CompletableFuture<List<Album>> quickSearch(String query, boolean exact) {
		return quickSearch(query, exact, repo);
	}

	@Override
	protected IModelRepo<Album> getRepo() {
		return repo;
	}

	@Override
	protected Album reloadIfNeeded(Album model) {
		if (model.getId() == null) {
			return model;
		}

		Album loaded = getByIdForEdition(model.getId());
		BeanUtils.copyProperties(model, loaded);
		return loaded;
	}

	@Transactional(rollbackFor = Throwable.class)
	@CacheEvict(cacheNames = "ModelLists", key = "#root.targetClass.simpleName.replaceAll('Service$', '')")
	@Override
	public long save(@NotNull Album newAlbum) {
		boolean isNewAlbum = newAlbum.getId() == null;
		boolean isLeavingWishlist = false;

		if (!isNewAlbum) {
			// Check if the album is leaving the wishlist
			Long newAlbumId = newAlbum.getId();
			Album oldAlbum = repo.findById(newAlbumId).orElseThrow(notFoundById(Album.class, newAlbum.getId()));
			isLeavingWishlist = oldAlbum.isWishlist() && !newAlbum.isWishlist();
			if (isLeavingWishlist) {
				LOGGER.debug("The saved album is leaving the wishlist");
			}
		}

		// Check if a book type was provided
		if (newAlbum.getBookType() == null) {
			if (bookTypeService.isManagementEnabled()) {
				throw new DemyoRuntimeException(DemyoErrorCode.BOOK_TYPE_MGMT_ENABLED_REQ_ON_ALBUM);
			}
			// Assign a default one
			newAlbum.setBookType(bookTypeService.findAll().get(0));
		}

		long id = super.save(newAlbum);

		if (newAlbum.isWishlist() && !isNewAlbum) {
			// If an existing album becomes part of the wishlist, remove it from the reading list of all users
			// It was probably a mistake from the user that it wasn't part of the wishlist in the first place
			// If it's a new album, there's no need to delete it: it couldn't possibly have been part of the
			// wishlist
			LOGGER.debug("Removing the album from the reading list of all users");
			readerRepo.deleteFromAllReadingLists(id);
		} else if (// Add it to the reading list of all users if...
		// New album, not part of the wishlist
		(isNewAlbum && !newAlbum.isWishlist())
				// Not a new album, but was part of the wishlist and is no longer part of it
				|| isLeavingWishlist) {
			LOGGER.debug("Adding the album to the reading list of all users (removing it first to be safe)");
			// This is to be safe: normally, it won't be needed but
			// 1) I once messed up my database manually so that items were simultaneously in the wishlist and reading
			// list
			// 2) If there is ever a bug that causes this corruption in Demyo without messing with the DB manually,
			// this will prevent the issue
			readerRepo.deleteFromAllReadingLists(id);
			readerRepo.insertInAllReadingLists(id);
		}

		return id;
	}

	@Transactional(rollbackFor = Throwable.class)
	@CacheEvict(cacheNames = "ModelLists", key = "#root.targetClass.simpleName.replaceAll('Service$', '')")
	@Override
	public void recoverFromFilePond(long albumId, String coverFilePondId, String[] otherFilePondIds)
			throws DemyoException {
		Album album = getByIdForEdition(albumId);
		String baseName = album.getBaseNameForImages();

		if (!StringUtils.isBlank(coverFilePondId)) {
			String coverBaseName = translationService.translateVargs("special.filepond.Album.baseCoverName", baseName);
			Image cover = imageService.recoverImagesFromFilePond(coverBaseName, false, coverFilePondId).get(0);
			album.setCover(cover);
		}

		if (otherFilePondIds != null && otherFilePondIds.length > 0) {
			String imageBaseName = translationService.translateVargs("special.filepond.Album.baseImageName", baseName);
			List<Image> images = imageService.recoverImagesFromFilePond(imageBaseName, true, otherFilePondIds);
			album.getImages().addAll(images);
		}

		save(album);
	}

	@Override
	public int countAlbumsByTag(long tagId) {
		return repo.countAlbumsByTag(tagId);
	}

	@Override
	public long countAlbumsByFilter(AlbumFilter filter) {
		return repo.count(filter.toPredicate());
	}
}
