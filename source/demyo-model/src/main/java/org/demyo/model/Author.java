package org.demyo.model;

import java.util.Date;
import java.util.SortedSet;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedSubgraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.SortComparator;
import org.hibernate.validator.constraints.URL;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.demyo.model.jackson.SortedSetDeserializer;
import org.demyo.model.util.AuthorComparator;
import org.demyo.model.util.DefaultOrder;
import org.demyo.model.util.StartsWithField;

/**
 * Represents an Author (Artist, Writer, Colorist, Inker...).
 */
@Entity
@Table(name = "AUTHORS")
@DefaultOrder(expression =
{ @DefaultOrder.Order(property = "name"), @DefaultOrder.Order(property = "firstName") })
@NamedEntityGraph(name = "Author.forList", attributeNodes = @NamedAttributeNode("pseudonymOf"))
@NamedEntityGraph(name = "Author.forEdition", attributeNodes =
{ @NamedAttributeNode("portrait"), @NamedAttributeNode("pseudonymOf") })
@NamedEntityGraph(name = "Author.forView", attributeNodes =
{ @NamedAttributeNode("portrait"), @NamedAttributeNode(value = "pseudonymOf", subgraph = "Author.subgraph.Author"),
	@NamedAttributeNode("pseudonyms") },
subgraphs = {
		@NamedSubgraph(name = "Author.subgraph.Author", attributeNodes = @NamedAttributeNode("portrait"))
	})
public class Author extends AbstractModel {
	/** The last name. */
	@Column
	@NotBlank
	@StartsWithField
	@JsonView(ModelView.Basic.class)
	private String name;
	/** The first name. */
	@Column(name = "fname")
	@JsonView(ModelView.Basic.class)
	private String firstName;
	/** The nickname. */
	@Column
	private String nickname;
	/** The biography. */
	@Column
	private String biography;
	/** The portrait. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "portrait_id")
	private Image portrait;
	/** The website. */
	@Column
	@URL
	private String website;

	/** The birth date of the author. */
	@Column(name = "birth")
	private Date birthDate;

	/** The death date of the author. */
	@Column(name = "death")
	private Date deathDate;

	/** The country of origin of the author. */
	@Column
	private String country;

	/** The real author behind this pseudonym. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pseudonym_of_id")
	@JsonIgnoreProperties({"biography", "birthDate", "deathDate", "country", "portrait"})
	private Author pseudonymOf;

	/** The pseudonyms for this author. */
	@OneToMany(mappedBy = "pseudonymOf", fetch = FetchType.LAZY)
	@BatchSize(size = BATCH_SIZE)
	@SortComparator(AuthorComparator.class)
	@JsonIgnoreProperties("pseudonymOf")
	@JsonDeserialize(using = SortedSetDeserializer.class)
	private SortedSet<Author> pseudonyms;

	@Override
	public String getIdentifyingName() {
		return getFullName();
	}

	/**
	 * Gets a human-readable identifier for this author that also includes the real author name if relevant.
	 *
	 * @return A string representing the author.
	 */
	@JsonView(ModelView.Minimal.class)
	public String getNameWithPseudonym() {
		StringBuilder sb = new StringBuilder(getIdentifyingName());
		// Some views may not load the pseudonym (like the Album edit)
		// Be wary of this and avoid lazy-init of the field at the wrong time
		if (pseudonymOf != null && Hibernate.isInitialized(pseudonymOf)) {
			sb.append(" (")
					.append(pseudonymOf.getIdentifyingName())
					.append(')');
		}
		return sb.toString();
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
	 * Gets the birth date of the author.
	 *
	 * @return the birth date of the author
	 */
	public Date getBirthDate() {
		return birthDate;
	}

	/**
	 * Sets the birth date of the author.
	 *
	 * @param birthDate the new birth date of the author
	 */
	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	/**
	 * Gets the death date of the author.
	 *
	 * @return the death date of the author
	 */
	public Date getDeathDate() {
		return deathDate;
	}

	/**
	 * Sets the death date of the author.
	 *
	 * @param deathDate the new death date of the author
	 */
	public void setDeathDate(Date deathDate) {
		this.deathDate = deathDate;
	}

	/**
	 * Gets the country of origin of the author.
	 *
	 * @return the country of origin of the author
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * Sets the country of origin of the author.
	 *
	 * @param country the new country of origin of the author.
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * Gets the real author behind this pseudonym.
	 *
	 * @return The real author behind this pseudonym
	 */
	public Author getPseudonymOf() {
		return pseudonymOf;
	}

	/**
	 * Sets the real author behind this pseudonym
	 *
	 * @param pseudonymOf The new real author behind this pseudonym
	 */
	public void setPseudonymOf(Author pseudonymOf) {
		this.pseudonymOf = pseudonymOf;
	}

	/**
	 * Gets the pseudonyms for this author.
	 *
	 * @return The pseudonyms for this author
	 */
	public SortedSet<Author> getPseudonyms() {
		return pseudonyms;
	}

	/**
	 * Sets the pseudonyms for this author.
	 *
	 * @param pseudonyms the new pseudonyms for this author
	 */
	public void setPseudonyms(SortedSet<Author> pseudonyms) {
		this.pseudonyms = pseudonyms;
	}
}
