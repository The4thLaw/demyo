package org.demyo.service;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonView;

import org.demyo.model.Album;
import org.demyo.model.Author;
import org.demyo.model.Collection;
import org.demyo.model.ModelView;
import org.demyo.model.Publisher;
import org.demyo.model.Series;
import org.demyo.model.Tag;
import org.demyo.model.Universe;

/** Represents the results of a search across multiple model types. */
public final class SearchResult {
	/** The matching {@link Universe}s. */
	@JsonView(ModelView.Basic.class)
	private final List<Universe> universes;
	/** The matching {@link Series}. */
	@JsonView(ModelView.Basic.class)
	private final List<Series> series;
	/** The matching {@link Album}s. */
	@JsonView(ModelView.Basic.class)
	private final List<Album> albums;
	/** The matching {@link Tag}s. */
	@JsonView(ModelView.Basic.class)
	private final List<Tag> tags;
	/** The matching {@link Author}s. */
	@JsonView(ModelView.Basic.class)
	private final List<Author> authors;
	/** The matching {@link Publisher}s. */
	@JsonView(ModelView.Basic.class)
	private final List<Publisher> publishers;
	/** The matching {@link Collection}s. */
	@JsonView(ModelView.Basic.class)
	private final List<Collection> collections;

	/** Creates a completely blank search result. */
	public SearchResult() {
		this.universes = Collections.emptyList();
		this.series = Collections.emptyList();
		this.albums = Collections.emptyList();
		this.tags = Collections.emptyList();
		this.authors = Collections.emptyList();
		this.publishers = Collections.emptyList();
		this.collections = Collections.emptyList();
	}

	/**
	 * Creates a search result with the provided matches.
	 *
	 * @param universes The matching {@link Universe}s.
	 * @param series The matching {@link Series}.
	 * @param albums The matching {@link Album}s.
	 * @param tags The matching {@link Tag}s.
	 * @param authors The matching {@link Author}s.
	 * @param publishers The matching {@link Publisher}s.
	 * @param collections The matching {@link Collection}s.
	 */
	public SearchResult(List<Universe> universes, List<Series> series, List<Album> albums,
			List<Tag> tags, List<Author> authors, List<Publisher> publishers,
			List<Collection> collections) {
		this.universes = universes;
		this.series = series;
		this.albums = albums;
		this.tags = tags;
		this.authors = authors;
		this.publishers = publishers;
		this.collections = collections;
	}

	/**
	 * Gets the matching {@link Universe}s.
	 *
	 * @return the matching {@link Universe}s
	 */
	public List<Universe> getUniverses() {
		return universes;
	}

	/**
	 * Gets the matching {@link Series}.
	 *
	 * @return the matching {@link Series}
	 */
	public List<Series> getSeries() {
		return series;
	}

	/**
	 * Gets the matching {@link Album}s.
	 *
	 * @return the matching {@link Album}s
	 */
	public List<Album> getAlbums() {
		return albums;
	}

	/**
	 * Gets the matching {@link Tag}s.
	 *
	 * @return the matching {@link Tag}s
	 */
	public List<Tag> getTags() {
		return tags;
	}

	/**
	 * Gets the matching {@link Author}s.
	 *
	 * @return the matching {@link Author}s
	 */
	public List<Author> getAuthors() {
		return authors;
	}

	/**
	 * Gets the matching {@link Publisher}s.
	 *
	 * @return the matching {@link Publisher}s
	 */
	public List<Publisher> getPublishers() {
		return publishers;
	}

	/**
	 * Gets the matching {@link Collection}s.
	 *
	 * @return the matching {@link Collection}s
	 */
	public List<Collection> getCollections() {
		return collections;
	}
}
