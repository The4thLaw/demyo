package org.demyo.service.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import org.demyo.dao.IReaderRepo;
import org.demyo.model.Album;
import org.demyo.model.Reader;
import org.demyo.model.Series;
import org.demyo.model.config.ApplicationConfiguration;
import org.demyo.service.IReaderContext;

/**
 * Implements the contract defined by {@link IReaderContext}.
 */
@Component
@Scope(scopeName = "session", proxyMode = ScopedProxyMode.INTERFACES)
public class ReaderContext implements IReaderContext {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReaderContext.class);

	@Autowired
	private IReaderRepo readerRepo;

	private Reader reader;
	private Set<Long> favouriteSeriesIds;
	private Set<Long> favouriteAlbumIds;
	private Set<Long> readingListIds;

	@Override
	public Reader getCurrentReader() {
		return reader;
	}

	@Override
	public ApplicationConfiguration getConfiguration() {
		if (reader != null) {
			return reader.getConfiguration();
		}
		LOGGER.debug("No reader selected, returning a default configuration as failsafe");
		return ApplicationConfiguration.getDefaultConfiguration();
	}

	@Override
	public void setCurrentReader(Reader r) {
		if (r == null) {
			throw new IllegalArgumentException("null reader");
		}
		reader = r;

		// Clear the cached sets
		favouriteSeriesIds = new HashSet<>();
		favouriteAlbumIds = new HashSet<>();
		readingListIds = new HashSet<>();

		// Reload the cached sets
		addAll(favouriteSeriesIds, readerRepo.getFavouriteSeriesForReader(r.getId()));
		addAll(favouriteAlbumIds, readerRepo.getFavouriteAlbumsForReader(r.getId()));
		addAll(readingListIds, readerRepo.getReadingListForReader(r.getId()));

		LOGGER.debug(
				"Size of the context for Reader {}: {} favourite Series, {} favourite Albums, {} Albums in the"
						+ " reading list",
				reader.getIdentifyingName(), favouriteSeriesIds.size(), favouriteAlbumIds.size(),
				readingListIds.size());
	}

	private static void addAll(Collection<Long> to, Iterable<Number> from) {
		// TODO:use Java 8 paralell streams?
		for (Number n : from) {
			to.add(n.longValue());
		}
	}

	@Override
	public void clearCurrentReader() {
		reader = null;
		favouriteSeriesIds = null;
		favouriteAlbumIds = null;
	}

	@Override
	public boolean isFavouriteSeries(Series s) {
		return favouriteSeriesIds.contains(s.getId());
	}

	@Override
	public boolean isFavouriteAlbum(Album a) {
		return favouriteAlbumIds.contains(a.getId());
	}

	@Override
	public boolean isAlbumInReadingList(Album a) {
		return readingListIds.contains(a.getId());
	}

}
