package org.demyo.model.behaviour;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.demyo.model.Taxon;
import org.demyo.model.enums.TaxonType;
import org.demyo.model.util.IdentifyingNameComparator;

public interface Taxonomized {
	/**
	 * Gets the {@link Taxon}s labelling this entity.
	 *
	 * @return the {@link Taxon}s labelling this entity
	 */
	@JsonIgnore
	SortedSet<Taxon> getTaxons();

	/**
	 * Sets the {@link Taxon}s labelling this entity.
	 *
	 * @param taxons the new {@link Taxon}s labelling this entity
	 */
	@JsonIgnore
	void setTaxons(SortedSet<Taxon> taxons);

	/**
	 * Gets the {@link Taxon}s labelling this entity as genres.
	 *
	 * @return the {@link Taxon}s labelling this entity as genres
	 */
	// TODO: #14: Make sure this doesn't mess with the Taxons when saving an album/series. The type must remain
	@JsonIgnoreProperties("type")
	default SortedSet<Taxon> getGenres() {
		return getTaxons().stream()
			.filter(t -> t.getType() == TaxonType.GENRE)
			.collect(Collectors.toCollection(() -> new TreeSet<Taxon>(new IdentifyingNameComparator())));
	}

	/**
	 * Gets the {@link Taxon}s labelling this entity as tags.
	 *
	 * @return the {@link Taxon}s labelling this entity as tags
	 */
	@JsonIgnoreProperties("type")
	default SortedSet<Taxon> getTags() {
		return getTaxons().stream()
			.filter(t -> t.getType() == TaxonType.TAG)
			.collect(Collectors.toCollection(() -> new TreeSet<Taxon>(new IdentifyingNameComparator())));
	}

	/**
	 * Sets the {@link Taxon}s labelling this entity as genres.
	 *
	 * <p>Implementations should store this in a transient cache that will not be persisted.
	 * Callers should call {@link #mergeTaxons()} to set the taxons when ready.
	 * </p>
	 *
	 * @param tags the new {@link Taxon}s labelling this entity
	 */
	void setGenres(SortedSet<Taxon> genres);

	/**
	 * Sets the {@link Taxon}s labelling this entity as tags.
	 *
	 * <p>Implementations should store this in a transient cache that will not be persisted.
	 * Callers should call {@link #mergeTaxons()} to set the taxons when ready.
	 * </p>
	 *
	 * @param tags the new {@link Taxon}s labelling this entity
	 */
	void setTags(SortedSet<Taxon> tags);

	/**
	 * Gets the genre recently set by {@link #setGenres(SortedSet)}.
	 *
	 * @return
	 */
	@JsonIgnore
	SortedSet<Taxon> getTransientGenreCache();

	/**
	 * Gets the tags recently set by {@link #setTags(SortedSet)}.
	 *
	 * @return
	 */
	@JsonIgnore
	SortedSet<Taxon> getTransientTagCache();

	/**
	 * Merges the transient caches of genres and tags into the taxons.
	 *
	 * <p>
	 * Once this method has been called, {@link #getTransientTagCache()} and {@link #getTags()} become
	 * equivalent.
	 * </p>
	 */
	default void mergeTaxons() {
		SortedSet<Taxon> taxons = new TreeSet<>(new IdentifyingNameComparator());
		taxons.addAll(getGenres());
		taxons.addAll(getTags());
		setTaxons(taxons);
	}
}
