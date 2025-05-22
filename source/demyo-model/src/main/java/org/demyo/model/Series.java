package org.demyo.model;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.hibernate.annotations.SortComparator;
import org.hibernate.validator.constraints.URL;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.demyo.model.jackson.SortedSetDeserializer;
import org.demyo.model.util.DefaultOrder;
import org.demyo.model.util.IdentifyingNameComparator;

/**
 * Represents a Series.
 */
@Entity
@Table(name = "SERIES")
@DefaultOrder(expression = @DefaultOrder.Order(property = "name"))
@NamedEntityGraph(name = "Series.forView", attributeNodes = @NamedAttributeNode("relatedSeries"))
public class Series extends AbstractNamedModel {
	/** The name in the Series' original language. */
	@Column(name = "original_name")
	private String originalName;
	/** The universe to which this Series belongs. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "universe_id")
	private Universe universe;
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
	@JsonIgnoreProperties("relatedSeries")
	@JsonDeserialize(using = SortedSetDeserializer.class)
	private SortedSet<Series> relatedSeries;

	/** The internal IDs of the Albums belonging to this Series. */
	@Transient
	private List<Long> albumIds;

	/** The {@link Reader}s who favourited this Series. */
	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "favouriteSeries")
	@SortComparator(IdentifyingNameComparator.class)
	private SortedSet<Reader> readersFavourites;

	/**
	 * Gets the name in the Series' original language.
	 *
	 * @return the name in the Series' original language
	 */
	public String getOriginalName() {
		return originalName;
	}

	/**
	 * Sets the name in the Series' original language.
	 *
	 * @param originalName the new name in the Series' original language
	 */
	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}

	/**
	 * Gets the universe to which this Series belongs.
	 * @return The universe to which this Series belongs
	 */
	public Universe getUniverse() {
		return universe;
	}

	/**
	 * Sets the universe to which this Series belongs.
	 * @param universe the new universe to which this Series belongs
	 */
	public void setUniverse(Universe universe) {
		this.universe = universe;
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
	 * Gets the internal IDs of the Albums belonging to this Series.
	 *
	 * @return the internal IDs of the Albums belonging to this Series
	 */
	public List<Long> getAlbumIds() {
		return albumIds;
	}

	/**
	 * Sets the internal IDs of the Albums belonging to this Series.
	 *
	 * @param albumIds the new internal IDs of the Albums belonging to this Series
	 */
	public void setAlbumIds(List<Long> albumIds) {
		this.albumIds = albumIds;
	}
}
