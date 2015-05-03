package org.demyo.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.demyo.model.util.DefaultOrder;
import org.demyo.model.util.StartsWithField;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

/**
 * Represents a Series.
 * 
 * @author $Author: xr $
 * @version $Revision: 1071 $
 */
@Entity
@Table(name = "SERIES")
@DefaultOrder(expression = @DefaultOrder.Order(property = "name"))
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
	/** The series related to this one. */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "series_relations", joinColumns = @JoinColumn(name = "main"),
			inverseJoinColumns = @JoinColumn(name = "sub"))
	@OrderBy(value = "name asc")
	private Set<Series> relatedSeries;

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
	public void setRelatedSeries(Set<Series> relatedSeries) {
		this.relatedSeries = relatedSeries;
	}
}
