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

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

/**
 * Represents an Author (Artist, Scenarist, Colorist, Inker...).
 * 
 * @author $Author: xr $
 * @version $Revision: 1076 $
 */
@Entity
@Table(name = "AUTHORS")
@DefaultOrder(expression = { @DefaultOrder.Order(property = "name"), @DefaultOrder.Order(property = "firstName") })
public class Author extends AbstractModel {
	/** The last name. */
	@Column(name = "name")
	@NotBlank
	@StartsWithField
	private String name;
	/** The first name. */
	@Column(name = "fname")
	private String firstName;
	/** The nickname. */
	@Column(name = "nickname")
	private String nickname;
	/** The biography. */
	@Column(name = "biography")
	private String biography;
	/** The portrait. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "portrait_id")
	private Image portrait;
	/** The portrait ID. */
	// Work around bug HHH-3718
	//@Column(name = "portrait_id", insertable = false, updatable = false)
	@Transient
	private Long portraitId;
	/** The website. */
	@Column(name = "website")
	@URL
	private String website;

	@Override
	public String getIdentifyingName() {
		return getFullName();
	}

	/**
	 * Gets the last name of the Author.
	 * 
	 * @return the last name of the Author
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the last name of the Author.
	 * 
	 * @param name the new last name of the Author
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the first name.
	 * 
	 * @return the first name
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * Sets the first name.
	 * 
	 * @param firstName the new first name
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * Gets the nickname.
	 * 
	 * @return the nickname
	 */
	public String getNickname() {
		return nickname;
	}

	/**
	 * Sets the nickname.
	 * 
	 * @param nickname the new nickname
	 */
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	/**
	 * Gets the biography.
	 * 
	 * @return the biography
	 */
	public String getBiography() {
		return biography;
	}

	/**
	 * Sets the biography.
	 * 
	 * @param biography the new biography
	 */
	public void setBiography(String biography) {
		this.biography = biography;
	}

	/**
	 * Gets the portrait.
	 * 
	 * @return the portrait
	 */
	public Image getPortrait() {
		return portrait;
	}

	/**
	 * Sets the portrait.
	 * 
	 * @param portrait the new portrait
	 */
	public void setPortrait(Image portrait) {
		this.portrait = portrait;
	}

	/**
	 * Gets the portrait ID.
	 * 
	 * @return the portrait ID
	 */
	public Long getPortraitId() {
		return portraitId;
	}

	/**
	 * Sets the portrait ID.
	 * 
	 * @param portraitId the new portrait ID
	 */
	public void setPortraitId(Long portraitId) {
		this.portraitId = portraitId;
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
	 * Returns the "full" author name, composed of his first name, last name and nickname.
	 * 
	 * @return The full name.
	 */
	public String getFullName() {
		StringBuilder sb = new StringBuilder();
		if (!StringUtils.isEmpty(firstName)) {
			sb.append(firstName).append(' ');
		}
		if (!StringUtils.isEmpty(nickname)) {
			sb.append("'").append(nickname).append("' ");
		}
		sb.append(name);
		return sb.toString().trim();
	}
}
