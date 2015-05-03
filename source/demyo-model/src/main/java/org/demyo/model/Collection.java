package org.demyo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.demyo.model.util.DefaultOrder;
import org.demyo.model.util.StartsWithField;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

/**
 * Represents a Collection of comics within a {@link Publisher}.
 * 
 * @author $Author: xr $
 * @version $Revision: 1084 $
 */
@Entity
@Table(name = "COLLECTIONS")
@DefaultOrder(expression = { @DefaultOrder.Order(property = "name") })
public class Collection extends AbstractModel {
	/** The name. */
	@Column(name = "name")
	@NotBlank
	@StartsWithField
	private String name;
	/** The website. */
	@Column(name = "website")
	@URL
	private String website;
	/** The RSS feed. */
	@Column(name = "feed")
	@URL
	private String feed;
	/** The history of this Collection. */
	@Column(name = "history")
	private String history;
	/** The logo (visual identity) of the Collection. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "logo_id")
	private Image logo;
	/** The logo ID. */
	// Work around bug HHH-3718
	@Transient
	private Long logoId;
	/** The parent Publisher of the Collection. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "publisher_id")
	private Publisher publisher;
	/** The Publisher ID. */
	// Work around bug HHH-3718
	@Transient
	private Long publisherId;

	@Override
	public String getIdentifyingName() {
		return getName();
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
	 * Gets the logo (visual identity) of the Collection.
	 * 
	 * @return the logo (visual identity) of the Collection
	 */
	public Image getLogo() {
		return logo;
	}

	/**
	 * Sets the logo (visual identity) of the Collection.
	 * 
	 * @param logo the new logo (visual identity) of the Collection
	 */
	public void setLogo(Image logo) {
		this.logo = logo;
	}

	/**
	 * Gets the logo ID.
	 * 
	 * @return the logo ID
	 */
	public Long getLogoId() {
		return logoId;
	}

	/**
	 * Sets the logo ID.
	 * 
	 * @param logoId the new logo ID
	 */
	public void setLogoId(Long logoId) {
		this.logoId = logoId;
	}

	/**
	 * Gets the parent Publisher of the Collection.
	 * 
	 * @return the parent Publisher of the Collection
	 */
	public Publisher getPublisher() {
		return publisher;
	}

	/**
	 * Sets the parent Publisher of the Collection.
	 * 
	 * @param publisher the new parent Publisher of the Collection
	 */
	public void setPublisher(Publisher publisher) {
		this.publisher = publisher;
	}

	/**
	 * Gets the Publisher ID.
	 * 
	 * @return the Publisher ID
	 */
	public Long getPublisherId() {
		return publisherId;
	}

	/**
	 * Sets the Publisher ID.
	 * 
	 * @param publisherId the new Publisher ID
	 */
	public void setPublisherId(Long publisherId) {
		this.publisherId = publisherId;
	}
}
