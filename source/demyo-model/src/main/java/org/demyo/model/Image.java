package org.demyo.model;

import java.util.SortedSet;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedSubgraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

import org.hibernate.annotations.SortComparator;
import org.the4thlaw.commons.utils.io.FilenameUtils;

import com.fasterxml.jackson.annotation.JsonView;

import org.demyo.model.util.AlbumAndSeriesComparator;
import org.demyo.model.util.AuthorComparator;
import org.demyo.model.util.DefaultOrder;
import org.demyo.model.util.IdentifyingNameComparator;

/**
 * Represents an image.
 */
@Entity
@Table(name = "IMAGES")
@DefaultOrder(expression = @DefaultOrder.Order(property = "description"))
@NamedEntityGraph(name = "Image.forDependencies",
	attributeNodes = { @NamedAttributeNode(value = "albumCovers", subgraph = "Image.subgraph.Album"),
		@NamedAttributeNode(value = "albumOtherImages", subgraph = "Image.subgraph.Album"),
		@NamedAttributeNode("authors"), @NamedAttributeNode("collections"),
		@NamedAttributeNode("derivatives"), @NamedAttributeNode("publishers") },
	subgraphs = @NamedSubgraph(name = "Image.subgraph.Album", attributeNodes = @NamedAttributeNode("series")))
public class Image extends AbstractModel {
	/** The URL to access the image. */
	@Column(name = "url")
	@NotBlank
	private String url;
	/** The description of the image. */
	@Column(name = "description")
	private String description;

	/** The {@link Album}s which use this Image as cover. */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "cover")
	@SortComparator(AlbumAndSeriesComparator.class)
	@JsonView(ModelView.ImageDependencies.class)
	private SortedSet<Album> albumCovers;

	/** The {@link Album}s which use this Image as other image. */
	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "images")
	@SortComparator(AlbumAndSeriesComparator.class)
	@JsonView(ModelView.ImageDependencies.class)
	private SortedSet<Album> albumOtherImages;

	/** The {@link Author}s who use this Image. */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "portrait")
	@SortComparator(AuthorComparator.class)
	@JsonView(ModelView.ImageDependencies.class)
	private SortedSet<Author> authors;

	/** The {@link Collection}s which use this Image. */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "logo")
	@SortComparator(IdentifyingNameComparator.class)
	@JsonView(ModelView.ImageDependencies.class)
	private SortedSet<Collection> collections;

	/** The {@link Derivative}s which use this Image. */
	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "images")
	@SortComparator(IdentifyingNameComparator.class)
	@JsonView(ModelView.ImageDependencies.class)
	private SortedSet<Derivative> derivatives;

	/** The {@link Publisher}s which use this Image. */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "logo")
	@SortComparator(IdentifyingNameComparator.class)
	@JsonView(ModelView.ImageDependencies.class)
	private SortedSet<Publisher> publishers;

	@Override
	public String getIdentifyingName() {
		return getDescription();
	}

	/**
	 * Gets a file name that makes sense to the user, regardless of the name on disk.
	 *
	 * @return a file name.
	 */
	@JsonView(ModelView.Basic.class)
	public String getUserFileName() {
		String fileExtension = FilenameUtils.getFileExtension(url);
		if (fileExtension == null) {
			// Reasonable default, more useful to most people than a ".dat" or something
			fileExtension = "jpg";
		}
		String basename = getIdentifyingName();
		return basename + "." + fileExtension;
	}

	/**
	 * Gets the URL to access the image.
	 *
	 * @return the URL to access the image
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Sets the URL to access the image.
	 *
	 * @param url the new URL to access the image
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Gets the description of the image.
	 *
	 * @return the description of the image
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description of the image.
	 *
	 * @param description the new description of the image
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the {@link Album}s which use this Image as cover.
	 *
	 * @return the {@link Album}s which use this Image as cover
	 */
	public SortedSet<Album> getAlbumCovers() {
		return albumCovers;
	}

	/**
	 * Sets the {@link Album}s which use this Image as cover.
	 *
	 * @param albumCovers the new {@link Album}s which use this Image as cover
	 */
	public void setAlbumCovers(SortedSet<Album> albumCovers) {
		this.albumCovers = albumCovers;
	}

	/**
	 * Gets the {@link Album}s which use this Image as other image.
	 *
	 * @return the {@link Album}s which use this Image as other image
	 */
	public SortedSet<Album> getAlbumOtherImages() {
		return albumOtherImages;
	}

	/**
	 * Sets the {@link Album}s which use this Image as other image.
	 *
	 * @param albumOtherImages the new {@link Album}s which use this Image as other image
	 */
	public void setAlbumOtherImages(SortedSet<Album> albumOtherImages) {
		this.albumOtherImages = albumOtherImages;
	}

	/**
	 * Gets the {@link Author}s who use this Image.
	 *
	 * @return the {@link Author}s who use this Image
	 */
	public SortedSet<Author> getAuthors() {
		return authors;
	}

	/**
	 * Sets the {@link Author}s who use this Image.
	 *
	 * @param authors the new {@link Author}s who use this Image
	 */
	public void setAuthors(SortedSet<Author> authors) {
		this.authors = authors;
	}

	/**
	 * Gets the {@link Collection}s which use this Image.
	 *
	 * @return the {@link Collection}s which use this Image
	 */
	public SortedSet<Collection> getCollections() {
		return collections;
	}

	/**
	 * Sets the {@link Collection}s which use this Image.
	 *
	 * @param collections the new {@link Collection}s which use this Image
	 */
	public void setCollections(SortedSet<Collection> collections) {
		this.collections = collections;
	}

	/**
	 * Gets the {@link Derivative}s which use this Image.
	 *
	 * @return the {@link Derivative}s which use this Image
	 */
	public SortedSet<Derivative> getDerivatives() {
		return derivatives;
	}

	/**
	 * Sets the {@link Derivative}s which use this Image.
	 *
	 * @param derivatives the new {@link Derivative}s which use this Image
	 */
	public void setDerivatives(SortedSet<Derivative> derivatives) {
		this.derivatives = derivatives;
	}

	/**
	 * Gets the {@link Publisher}s which use this Image.
	 *
	 * @return the {@link Publisher}s which use this Image
	 */
	public SortedSet<Publisher> getPublishers() {
		return publishers;
	}

	/**
	 * Sets the {@link Publisher}s which use this Image.
	 *
	 * @param publishers the new {@link Publisher}s which use this Image
	 */
	public void setPublishers(SortedSet<Publisher> publishers) {
		this.publishers = publishers;
	}
}
