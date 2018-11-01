package org.demyo.service.impl;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

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
		favouriteSeriesIds = new HashSet<>();
		favouriteAlbumIds = new HashSet<>();
		readingListIds = new HashSet<>();

		if (r.getFavouriteSeries() != null) {
			for (Series s : r.getFavouriteSeries()) {
				favouriteSeriesIds.add(s.getId());
			}
		}
		if (r.getFavouriteAlbums() != null) {
			for (Album a : r.getFavouriteAlbums()) {
				favouriteAlbumIds.add(a.getId());
			}
		}
		if (r.getReadingList() != null) {
			for (Album a : r.getReadingList()) {
				readingListIds.add(a.getId());
			}
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
