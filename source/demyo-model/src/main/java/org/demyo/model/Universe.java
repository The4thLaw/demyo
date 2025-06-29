package org.demyo.model;

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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.SortComparator;
import org.hibernate.validator.constraints.URL;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.demyo.model.jackson.SortedSetDeserializer;
import org.demyo.model.util.DefaultOrder;
import org.demyo.model.util.IdentifyingNameComparator;

/**
 * Represents a universe, a link between multiple series or albums.
 */
@Entity
@Table(name = "UNIVERSES")
@DefaultOrder(expression = @DefaultOrder.Order(property = "name"))
@NamedEntityGraph(name = "Universe.forEdition", attributeNodes =
{ @NamedAttributeNode("logo"), @NamedAttributeNode("images") })
@NamedEntityGraph(name = "Universe.forView", attributeNodes =
{ @NamedAttributeNode("logo"), @NamedAttributeNode("images"),
		@NamedAttributeNode("series"), @NamedAttributeNode("albums") })
public class Universe extends AbstractNamedModel {
	/** The description. */
	@Column(name = "description")
	private String description;

	/** The logo. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "logo_id")
	private Image logo;

	/** The website. */
	@Column(name = "website")
	@URL
	private String website;

	/** The {@link Image}s related to this Universe. */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "universes_images", joinColumns = @JoinColumn(name = "universe_id"), //
			inverseJoinColumns = @JoinColumn(name = "image_id"))
	@BatchSize(size = BATCH_SIZE)
	@SortComparator(IdentifyingNameComparator.class)
	@JsonDeserialize(using = SortedSetDeserializer.class)
	private SortedSet<Image> images;

	/** The series belonging to this universe. */
	@OneToMany(mappedBy = "universe", fetch = FetchType.LAZY)
	@BatchSize(size = BATCH_SIZE)
	@SortComparator(IdentifyingNameComparator.class)
	@JsonIgnoreProperties("universe")
	@JsonDeserialize(using = SortedSetDeserializer.class)
	private SortedSet<Series> series;

	/** The album belonging to this universe. */
	@OneToMany(mappedBy = "universe", fetch = FetchType.LAZY)
	@BatchSize(size = BATCH_SIZE)
	@SortComparator(IdentifyingNameComparator.class)
	@JsonIgnoreProperties("universe")
	@JsonDeserialize(using = SortedSetDeserializer.class)
	private SortedSet<Album> albums;

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description.
	 *
	 * @param images the new description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the logo.
	 *
	 * @return the logo
	 */
	public Image getLogo() {
		return logo;
	}

	/**
	 * Sets the logo.
	 *
	 * @param images the new logo
	 */
	public void setLogo(Image logo) {
		this.logo = logo;
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
	 * @param images the new website
	 */
	public void setWebsite(String website) {
		this.website = website;
	}

	/**
	 * Gets the {@link Image}s related to this Universe
	 *
	 * @return the {@link Image}s related to this Universe
	 */
	public SortedSet<Image> getImages() {
		return images;
	}

	/**
	 * Sets the {@link Image}s related to this Universe.
	 *
	 * @param images the new {@link Image}s related to this Universe
	 */
	public void setImages(SortedSet<Image> images) {
		this.images = images;
	}

	/**
	 * Gets the series belonging to this universe.
	 *
	 * @return the series belonging to this universe
	 */
	public SortedSet<Series> getSeries() {
		return series;
	}

	/**
	 * Sets the series belonging to this universe.
	 *
	 * @param series the new series belonging to this universe
	 */
	public void setSeries(SortedSet<Series> series) {
		this.series = series;
	}

	/**
	 * Gets the albums belonging to this universe.
	 *
	 * @return the albums belonging to this universe
	 */
	public SortedSet<Album> getAlbums() {
		return albums;
	}

	/**
	 * Sets the albums belonging to this universe.
	 *
	 * @param albums the new albums belonging to this universe
	 */
	public void setAlbums(SortedSet<Album> albums) {
		this.albums = albums;
	}

}
