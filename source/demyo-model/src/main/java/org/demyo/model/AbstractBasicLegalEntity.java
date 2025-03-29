package org.demyo.model;


import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import org.hibernate.validator.constraints.URL;
import org.springframework.lang.Nullable;

/**
 * Defines basic fields for a legal entity.
 */
@MappedSuperclass
public abstract class AbstractBasicLegalEntity extends AbstractNamedModel {
	/** The entity website. */
	@Column(name = "website")
	@URL
	@Nullable
	private String website;

	/** The history of this entity. */
	@Column(name = "history")
	@Nullable
	private String history;

	/**
	 * Gets the entity website.
	 *
	 * @return the entity website
	 */
	public String getWebsite() {
		return website;
	}

	/**
	 * Sets the entity website.
	 *
	 * @param website the new entity website
	 */
	public void setWebsite(String website) {
		this.website = website;
	}

	/**
	 * Gets the history of this entity.
	 *
	 * @return the history of this entity
	 */
	public String getHistory() {
		return history;
	}

	/**
	 * Sets the history of this entity.
	 *
	 * @param history the new history of this entity
	 */
	public void setHistory(String history) {
		this.history = history;
	}

}
