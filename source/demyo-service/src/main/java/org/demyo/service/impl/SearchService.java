package org.demyo.service.impl;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.demyo.model.Album;
import org.demyo.model.Author;
import org.demyo.model.Collection;
import org.demyo.model.Publisher;
import org.demyo.model.Series;
import org.demyo.model.Taxon;
import org.demyo.model.Universe;
import org.demyo.service.IAlbumService;
import org.demyo.service.IAuthorService;
import org.demyo.service.ICollectionService;
import org.demyo.service.IPublisherService;
import org.demyo.service.ISearchService;
import org.demyo.service.ISeriesService;
import org.demyo.service.ITaxonService;
import org.demyo.service.IUniverseService;
import org.demyo.service.SearchResult;
import org.demyo.utils.logging.LoggingSanitizer;

/**
 * Implements the contract defined by {@link ISearchService}.
 */
@Service
public class SearchService implements ISearchService {
	private static final Logger LOGGER = LoggerFactory.getLogger(SearchService.class);
	private static final int QS_WILDCARD_MIN_LENGTH = 2;

	@Autowired
	private IUniverseService universeService;
	@Autowired
	private ISeriesService seriesService;
	@Autowired
	private IAlbumService albumService;
	@Autowired
	private ITaxonService tagService;
	@Autowired
	private IAuthorService authorService;
	@Autowired
	private IPublisherService publisherService;
	@Autowired
	private ICollectionService collectionService;

	@Override
	public SearchResult quickSearch(String query) {
		StopWatch sw = new StopWatch();
		sw.start();

		if (StringUtils.isBlank(query)) {
			return new SearchResult();
		}

		boolean exactMatch = true;
		if (QS_WILDCARD_MIN_LENGTH < query.length()) {
			exactMatch = false;
			query = "%" + query + "%";
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Query on {} will be exact: {}", LoggingSanitizer.sanitize(query), exactMatch);
		}

		CompletableFuture<List<Universe>> universes = universeService.quickSearch(query, exactMatch);
		CompletableFuture<List<Series>> series = seriesService.quickSearch(query, exactMatch);
		CompletableFuture<List<Album>> albums = albumService.quickSearch(query, exactMatch);
		CompletableFuture<List<Taxon>> tags = tagService.quickSearch(query, exactMatch);
		CompletableFuture<List<Author>> authors = authorService.quickSearch(query, exactMatch);
		CompletableFuture<List<Publisher>> publishers = publisherService.quickSearch(query, exactMatch);
		CompletableFuture<List<Collection>> collections = collectionService.quickSearch(query, exactMatch);

		SearchResult result = new SearchResult(universes.join(), series.join(), albums.join(), tags.join(),
				authors.join(), publishers.join(), collections.join());

		sw.stop();
		LOGGER.debug("Parallel search performed in {}ms", sw.getTime());

		return result;
	}
}
