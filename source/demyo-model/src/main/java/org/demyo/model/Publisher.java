package org.demyo.model;

import java.util.SortedSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.SortComparator;
import org.hibernate.validator.constraints.URL;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.demyo.model.jackson.SortedSetDeserializer;
import org.demyo.model.util.DefaultOrder;
import org.demyo.model.util.IdentifyingNameComparator;

/**
 * Represents a Publisher of comics.
 */
@Entity
@Table(name = "PUBLISHERS")
@DefaultOrder(expression =
{ @DefaultOrder.Order(property = "name") })
@NamedEntityGraph(name = "Publisher.forIndex", attributeNodes = @NamedAttributeNode("collections"))
@NamedEntityGraph(name = "Publisher.forView", attributeNodes =
{ @NamedAttributeNode("collections"),
		@NamedAttributeNode("logo") })
@NamedEntityGraph(name = "Publisher.forEdition", attributeNodes = @NamedAttributeNode("logo"))
public class Publisher extends AbstractNamedModel {
	/** The website. */
	@Column(name = "website")
	@URL
	private String website;
	/** The RSS feed. */
	@Column(name = "feed")
	@URL
	private String feed;
	/** The history of this Publisher. */
	@Column(name = "history")
	private String history;
	/** The logo (visual identity) of the Publisher. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "logo_id")
	private Image logo;
	/** The collections belonging to this publisher. */
	@OneToMany(mappedBy = "publisher", fetch = FetchType.LAZY)
	@BatchSize(size = BATCH_SIZE)
	@SortComparator(IdentifyingNameComparator.class)
	@JsonIgnoreProperties("publisher")
	@JsonView(ModelView.Basic.class)
	@JsonDeserialize(using = SortedSetDeserializer.class)
	private SortedSet<Collection> collections;

	/**
	 * Gets the history of this Publisher.
	 * 
	 * @return the history of this Publisher
	 */
	public String getHistory() {
		return history;
	}

	/**
	 * Sets the history of this Publisher.
	 * 
	 * @param history the new history of this Publisher
	 */
	public void setHistory(String history) {
		this.history = history;
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
	 * Gets the RSS feed.
	 * 
	 * @return the RSS feed
	 */
	public String getFeed() {
		return feed;
	}

	/**
	 * Sets the RSS feed.
	 * 
	 * @param feed the new RSS feed
	 */
	public void setFeed(String feed) {
		this.feed = feed;
	}

	/**
	 * Gets the logo (visual identity) of the Publisher.
	 * 
	 * @return the logo (visual identity) of the Publisher
	 */
	public Image getLogo() {
		return logo;
	}

	/**
	 * Sets the logo (visual identity) of the Publisher.
	 * 
	 * @param logo the new logo (visual identity) of the Publisher
	 */
	public void setLogo(Image logo) {
		this.logo = logo;
	}

	/**
	 * Gets the collections belonging to this publisher.
	 * 
	 * @return the collections belonging to this publisher
	 */
	public SortedSet<Collection> getCollections() {
		return collections;
	}

}
