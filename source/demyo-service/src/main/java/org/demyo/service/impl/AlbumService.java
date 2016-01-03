package org.demyo.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.demyo.dao.IAlbumRepo;
import org.demyo.dao.IMetaSeriesRepo;
import org.demyo.dao.IModelRepo;
import org.demyo.dao.ISeriesRepo;
import org.demyo.model.Album;
import org.demyo.model.MetaSeries;
import org.demyo.service.IAlbumService;
import org.demyo.service.IConfigurationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implements the contract defined by {@link IAlbumService}.
 */
@Service
public class AlbumService extends AbstractModelService<Album> implements IAlbumService {
	private static final Logger LOGGER = LoggerFactory.getLogger(AlbumService.class);

	@Autowired
	private IAlbumRepo repo;
	@Autowired
	private IMetaSeriesRepo metaSeriesRepo;
	@Autowired
	private IConfigurationService configurationService;
	@Autowired
	ISeriesRepo seriesRepo;

	/**
	 * Default constructor.
	 */
	public AlbumService() {
		super(Album.class);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This specific implementation pages by series.
	 * </p>
	 */
	@Transactional(readOnly = true)
	@Override
	public Slice<Album> findPaginated(int currentPage, Order... orders) {
		// Adjust the page number: Spring Data counts from 0
		currentPage--;

		if (orders.length > 0) {
			throw new UnsupportedOperationException("It is not possible to override the order for pages of albums");
		}
		Pageable pageable = new PageRequest(currentPage, configurationService.getConfiguration()
				.getPageSizeForAlbums());

		Slice<MetaSeries> metaSlice = metaSeriesRepo.findAll(pageable);

		// At the very least, there will be that many elements
		List<Album> albumList = new ArrayList<Album>(metaSlice.getNumberOfElements());
		// Add all albums
		for (MetaSeries meta : metaSlice) {
			if (meta.getSeries() != null) {
				LOGGER.debug("Adding all {} albums from {}", meta.getSeries().getAlbums().size(), meta.getSeries()
						.getIdentifyingName());
				albumList.addAll(meta.getSeries().getAlbums());
			} else {
				LOGGER.debug("Adding one shot album: {}", meta.getAlbum().getIdentifyingName());
				albumList.add(meta.getAlbum());
			}
		}

		return new SliceImpl<Album>(albumList, pageable, metaSlice.hasNext());
	}

	@Override
	@Transactional(readOnly = true)
	public Album getAlbumTemplateForSeries(long seriesId) {
		Sort sort = new Sort(Direction.DESC, "cycle", "number", "numberSuffix", "title");
		Album last = repo.findTopBySeriesId(seriesId, sort);

		Album template = new Album();

		if (last != null) {
			template.setArtists(last.getArtists());
			template.setBinding(last.getBinding());
			template.setCollection(last.getCollection());
			template.setColorists(last.getColorists());
			template.setHeight(last.getHeight());
			template.setPages(last.getPages());
			template.setPublisher(last.getPublisher());
			template.setSeries(last.getSeries());
			template.setTags(last.getTags());
			template.setWidth(last.getWidth());
			template.setWriters(last.getWriters());
		} else {
			template.setSeries(seriesRepo.findOne(seriesId));
		}

		return template;
	}

	@Override
	protected IModelRepo<Album> getRepo() {
		return repo;
	}
}
