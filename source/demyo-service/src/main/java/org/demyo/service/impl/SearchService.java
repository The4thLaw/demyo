package org.demyo.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.demyo.model.Album;
import org.demyo.model.Author;
import org.demyo.model.Collection;
import org.demyo.model.IModel;
import org.demyo.model.Publisher;
import org.demyo.model.Series;
import org.demyo.model.Tag;
import org.demyo.service.IAlbumService;
import org.demyo.service.IAuthorService;
import org.demyo.service.ICollectionService;
import org.demyo.service.IPublisherService;
import org.demyo.service.ISearchService;
import org.demyo.service.ISeriesService;
import org.demyo.service.ITagService;
import org.demyo.service.SearchResult;

/**
 * Implements the contract defined by {@link ISearchService}.
 */
@Service
public class SearchService implements ISearchService {
	private static final Logger LOGGER = LoggerFactory.getLogger(SearchService.class);
	private static final int QS_WILDCARD_MIN_LENGTH = 2;

	@Autowired
	private ISeriesService seriesService;
	@Autowired
	private IAlbumService albumService;
	@Autowired
	private ITagService tagService;
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
		LOGGER.debug("Query on {} will be exact: {}", query, exactMatch);

		Future<List<Series>> series = seriesService.quickSearch(query, exactMatch);
		Future<List<Album>> albums = albumService.quickSearch(query, exactMatch);
		Future<List<Tag>> tags = tagService.quickSearch(query, exactMatch);
		Future<List<Author>> authors = authorService.quickSearch(query, exactMatch);
		Future<List<Publisher>> publishers = publisherService.quickSearch(query, exactMatch);
		Future<List<Collection>> collections = collectionService.quickSearch(query, exactMatch);

		// TODO: Spring 5: switch to CompletableFuture and use CF.allOf(...). It should remove the need for all the
		// getFuture calls, by using the join(). At that time, also check Spring's pool for the Async tasks
		SearchResult result = new SearchResult(getFuture(series), getFuture(albums), getFuture(tags),
				getFuture(authors), getFuture(publishers), getFuture(collections));

		sw.stop();
		LOGGER.debug("Parallel search performed in {}ms", sw.getTime());

		return result;
	}

	private static <M extends IModel> List<M> getFuture(Future<List<M>> future) {
		try {
			return future.get();
		} catch (InterruptedException | ExecutionException e) {
			LOGGER.error("Error while getting future task", e);
			return Collections.emptyList();
		}
	}
}
