package org.demyo.model;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.SortComparator;
import org.hibernate.validator.constraints.URL;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.demyo.model.jackson.SortedSetDeserializer;
import org.demyo.model.util.DefaultOrder;
import org.demyo.model.util.IdentifyingNameComparator;
import org.demyo.model.util.StartsWithField;

/**
 * Represents a Series.
 */
@Entity
@Table(name = "SERIES")
@DefaultOrder(expression = @DefaultOrder.Order(property = "name"))
@NamedEntityGraphs(
{
		@NamedEntityGraph(name = "Series.forView", attributeNodes = @NamedAttributeNode("relatedSeries")) })
public class Series extends AbstractModel {
	/** The name. */
	@Column(name = "name")
	@NotBlank
	@StartsWithField
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

	@Override
	public String getIdentifyingName() {
		return name;
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
