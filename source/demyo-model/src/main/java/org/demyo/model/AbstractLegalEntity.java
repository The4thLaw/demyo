package org.demyo.model;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import org.hibernate.validator.constraints.URL;

/**
 * Defines basic fields for a legal entity.
 */
@MappedSuperclass
public abstract class AbstractLegalEntity extends AbstractBasicLegalEntity {
	/** The RSS feed. */
	@Column(name = "feed")
	@URL
	private String feed;

	/** The logo (visual identity) of the entity. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "logo_id")
	private Image logo;

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

}
