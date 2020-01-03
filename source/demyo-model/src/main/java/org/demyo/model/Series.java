package org.demyo.model;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.NamedSubgraph;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.SortComparator;
import org.hibernate.validator.constraints.URL;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import org.demyo.model.util.AlbumComparator;
import org.demyo.model.util.DefaultOrder;
import org.demyo.model.util.IdentifyingNameComparator;
import org.demyo.model.util.StartsWithField;

/**
 * Represents a Series.
 */
@Entity
@Table(name = "SERIES")
@DefaultOrder(expression = @DefaultOrder.Order(property = "name"))
@NamedEntityGraphs({
		@NamedEntityGraph(name = "Series.forView", attributeNodes =
		{ @NamedAttributeNode("relatedSeries"),
				@NamedAttributeNode(value = "albums", subgraph = "Series.Album") }, subgraphs =
				{ @NamedSubgraph(name = "Series.Album", attributeNodes = { @NamedAttributeNode("writers"),
						@NamedAttributeNode("artists"), @NamedAttributeNode("colorists"), @NamedAttributeNode("inkers"),
						@NamedAttributeNode("translators"), @NamedAttributeNode("publisher"),
						@NamedAttributeNode("collection"), @NamedAttributeNode("tags"),
						@NamedAttributeNode("cover") }) }),
		@NamedEntityGraph(name = "Series.forEdition", attributeNodes = @NamedAttributeNode("relatedSeries")) })
public class Series extends AbstractModel {
	/** The name. */
	@Column(name = "name")
	@NotBlank
	@StartsWithField
	@JsonView(ModelView.Basic.class)
	private String name;
	/** The summary. */
	@Column(name = "summary")
	private String summary;
	/** The comment. */
	@Column(name = "comment")
	private String comment;
	/** The website. */
	@Column(name = "website")
	@URL
	private String website;
	/** The flag indicating whether the series is completed. */
	@Column(name = "completed")
	private Boolean completed;
	/** The physical location of this Series. */
	@Column(name = "location")
	private String location;
	/** The series related to this one. */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "series_relations", joinColumns = @JoinColumn(name = "main"), //
			inverseJoinColumns = @JoinColumn(name = "sub"))
	@SortComparator(IdentifyingNameComparator.class)
	private SortedSet<Series> relatedSeries;

	/** The albums belonging to this series. */
	@OneToMany(mappedBy = "series", fetch = FetchType.LAZY)
	@SortComparator(AlbumComparator.class)
	private SortedSet<Album> albums;

	/** The {@link Reader}s who favourited this Series. */
	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "favouriteSeries")
	@SortComparator(IdentifyingNameComparator.class)
	private SortedSet<Reader> readersFavourites;

	@Override
	public String getIdentifyingName() {
		return name;
	}

	private <A extends IModel, C extends java.util.Collection<A>> SortedSet<A> getAggregate(
			Function<Album, C> extractor) {
		SortedSet<A> aggregate = new TreeSet<>(new IdentifyingNameComparator());

		// Technically, we could use the stream but it's less efficient and this is just as readable
		for (Album a : albums) {
			aggregate.addAll(extractor.apply(a));
		}

		return aggregate;

	}

	/**
	 * Returns all tags used by the albums of this series.
	 * 
	 * @return the {@link Tag} set.
	 */
	@JsonIgnore
	public SortedSet<Tag> getAlbumTags() {
		return this.getAggregate(Album::getTags);
	}

	/**
	 * Returns all the writers who participated to the albums of this series.
	 * 
	 * @return the {@link Author} set.
	 */
	@JsonIgnore
	public SortedSet<Author> getAlbumWriters() {
		// return this.getAggregate(Album::getWriters);
		SortedSet<Author> albumAuthors = new TreeSet<>(new IdentifyingNameComparator());

		for (Album a : albums) {
			albumAuthors.addAll(a.getWriters());
		}

		return albumAuthors;
	}

	/**
	 * Returns all the artists who participated to the albums of this series.
	 * 
	 * @return the {@link Author} set.
	 */
	@JsonIgnore
	public SortedSet<Author> getAlbumArtists() {
		return this.getAggregate(Album::getArtists);
	}

	/**
	 * Returns all the colorists who participated to the albums of this series.
	 * 
	 * @return the {@link Author} set.
	 */
	@JsonIgnore
	public SortedSet<Author> getAlbumColorists() {
		return this.getAggregate(Album::getColorists);
	}

	/**
	 * Returns all the inkers who participated to the albums of this series.
	 * 
	 * @return the {@link Author} set.
	 */
	@JsonIgnore
	public SortedSet<Author> getAlbumInkers() {
		return this.getAggregate(Album::getColorists);
	}

	/**
	 * Returns all the translators who participated to the albums of this series.
	 * 
	 * @return the {@link Author} set.
	 */
	@JsonIgnore
	public SortedSet<Author> getAlbumTranslators() {
		return this.getAggregate(Album::getTranslators);
	}

	/**
	 * Gets the number of {@link Album}s encoded in the library for this Series, and actually owned.
	 * 
	 * @return the album count (excl. wishlist)
	 */
	@JsonIgnore
	public int getOwnedAlbumCount() {
		return Math.toIntExact(albums.stream()
				.filter(Album::isWishlist)
				.count());
	}

	/**
	 * Gets the number of {@link Album}s encoded in the library for this Series.
	 * 
	 * @return the album count (incl. wishlist)
	 */
	@JsonIgnore
	public int getTotalAlbumCount() {
		return albums.size();
	}

	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 * 
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the summary.
	 * 
	 * @return the summary
	 */
	public String getSummary() {
		return summary;
	}

	/**
	 * Sets the summary.
	 * 
	 * @param summary the new summary
	 */
	public void setSummary(String summary) {
		this.summary = summary;
	}

	/**
	 * Gets the comment.
	 * 
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * Sets the comment.
	 * 
	 * @param comment the new comment
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * Gets the website.
	 * 
	 * @return the website
	 */
	public String getWebsite() {
		return website;
	}

	/**
	 * Sets the website.
	 * 
	 * @param website the new website
	 */
	public void setWebsite(String website) {
		this.website = website;
	}

	/**
	 * Gets the flag indicating whether the series is completed.
	 * 
	 * @return the flag indicating whether the series is completed
	 */
	public Boolean getCompleted() {
		return completed;
	}

	/**
	 * Sets the flag indicating whether the series is completed.
	 * 
	 * @param completed the new flag indicating whether the series is completed
	 */
	public void setCompleted(Boolean completed) {
		this.completed = completed;
	}

	/**
	 * Gets the physical location of this Series.
	 * 
	 * @return the physical location of this Series
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * Sets the physical location of this Series.
	 * 
	 * @param location the new physical location of this Series
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * Gets the series related to this one.
	 * 
	 * @return the series related to this one
	 */
	public Set<Series> getRelatedSeries() {
		return relatedSeries;
	}

	/**
	 * Sets the series related to this one.
	 * 
	 * @param relatedSeries the new series related to this one
	 */
	public void setRelatedSeries(SortedSet<Series> relatedSeries) {
		this.relatedSeries = relatedSeries;
	}

	/**
	 * Gets the albums belonging to this series.
	 * 
	 * @return the albums belonging to this series
	 */
	public SortedSet<Album> getAlbums() {
		return albums;
	}
}
