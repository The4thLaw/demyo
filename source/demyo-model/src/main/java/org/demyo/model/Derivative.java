/*
 *
 */
package org.demyo.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.SortedSet;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedSubgraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import org.hibernate.Hibernate;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.SortComparator;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.demyo.model.constraints.OneNotNull;
import org.demyo.model.jackson.SortedSetDeserializer;
import org.demyo.model.util.ComparableComparator;
import org.demyo.model.util.IdentifyingNameComparator;

/**
 * Derivatives of {@link Album}s or {@link Series}.
 */
@Entity
@Table(name = "DERIVATIVES")
@NamedEntityGraph(name = "Derivative.forIndex", attributeNodes =
{ @NamedAttributeNode("series"), @NamedAttributeNode("album"), @NamedAttributeNode("type"),
		@NamedAttributeNode("source"), @NamedAttributeNode("images") })
@NamedEntityGraph(name = "Derivative.forEdition", attributeNodes =
{ @NamedAttributeNode("series"), @NamedAttributeNode("album"),
		@NamedAttributeNode(value = "artist", subgraph = "Derivative.subgraph.Author"),
		@NamedAttributeNode("type"), @NamedAttributeNode("source"), @NamedAttributeNode("images"),
		@NamedAttributeNode("prices") },
		subgraphs = @NamedSubgraph(name = "Derivative.subgraph.Author", attributeNodes = @NamedAttributeNode("pseudonymOf")))
@OneNotNull(fields =
{ "series.id", "album.id" })
public class Derivative extends AbstractPricedModel<DerivativePrice, Derivative> {
	/** The parent {@link Series}. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "series_id")
	@JsonView(ModelView.Basic.class)
	private Series series;

	/** The parent {@link Album}. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "album_id")
	@JsonView(ModelView.Basic.class)
	private Album album;

	/** The {@link Author} who worked on this Derivative. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "artist_id")
	@BatchSize(size = BATCH_SIZE)
	private Author artist;

	/** The {@link DerivativeType type} of this Derivative. */
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "derivative_type_id")
	private DerivativeType type;

	/** The number of colours. */
	@Column(name = "colors")
	private Integer colours;

	/** The {@link DerivativeSource source} of this Derivative. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "source_id")
	@JsonView(ModelView.Basic.class)
	private DerivativeSource source;

	/** The item number. */
	@Column(name = "number")
	private Integer number;

	/** The total count of items. */
	@Column(name = "total")
	private Integer total;

	/** The flag indicating whether this Derivative is signed by its artist. */
	@Column(name = "signed")
	private boolean signed;

	/** The flag indicating whether this Derivative is an artist's personal copy. */
	@Column(name = "authors_copy")
	private boolean authorsCopy;

	/** The flag indicating whether this Derivative is marked as restricted sale. */
	@Column(name = "restricted_sale")
	private boolean restrictedSale;

	/** The description. */
	@Column(name = "description")
	private String description;

	/** The height of the Album. */
	@Column(name = "height")
	@Min(0)
	private BigDecimal height;

	/** The width of the Album. */
	@Column(name = "width")
	@Min(0)
	private BigDecimal width;

	/** The width of the Album. */
	@Column(name = "depth")
	@Min(0)
	private BigDecimal depth;

	/** The prices applicable to the Derivative. */
	@OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
	// Not insertable or updatable: managed by the child entity
	@JoinColumn(name = "derivative_id", insertable = false, updatable = false)
	@SortComparator(ComparableComparator.class)
	@Valid
	@JsonManagedReference
	private SortedSet<DerivativePrice> prices;

	/** The {@link Image}s related to this Derivative. */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "derivatives_images", joinColumns = @JoinColumn(name = "derivative_id"), //
			inverseJoinColumns = @JoinColumn(name = "image_id"))
	@BatchSize(size = BATCH_SIZE)
	@SortComparator(IdentifyingNameComparator.class)
	@JsonDeserialize(using = SortedSetDeserializer.class)
	private SortedSet<Image> images;

	@Override
	protected Derivative self() {
		return this;
	}

	@Override
	public String getIdentifyingName() {
		StringBuilder sb = new StringBuilder();
		if (series != null) {
			sb.append(series.getIdentifyingName()).append(" - ");
		}
		if (album != null) {
			sb.append(album.getIdentifyingName()).append(" - ");
		}
		if (type != null) {
			// null would never happen on saved entities but could happen in unsaved ones using toString
			sb.append(type.getIdentifyingName());
		}
		if (source != null) {
			sb.append(" ").append(source.getIdentifyingName());
		}
		return sb.toString();
	}

	/**
	 * Returns the base name that can be used for automatic image descriptions.
	 *
	 * @return The name.
	 */
	public String getBaseNameForImages() {
		StringBuilder sb = new StringBuilder();

		if (album != null) {
			sb.append(album.getBaseNameForImages());
		} else if (series != null) {
			sb.append(series.getIdentifyingName());
		}

		if (!sb.isEmpty()) {
			sb.append(" - ");
		}

		sb.append(type.getIdentifyingName());
		if (source != null) {
			sb.append(" ").append(source.getIdentifyingName());
		}
		return sb.toString();
	}

	/**
	 * Returns the main image associated to a Derivative. The one that is supposed to represent it.
	 *
	 * @return An Image, or <code>null</code> if no image is associated to this Derivative.
	 */
	@Transient
	@JsonView(ModelView.Basic.class)
	public Image getMainImage() {
		// In some cases (such as when loading a Derivative from its Image), this collection could be
		// uninitialized
		if (!Hibernate.isInitialized(images) || images == null || images.isEmpty()) {
			return null;
		}
		return images.iterator().next();
	}

	/**
	 * Gets the parent {@link Series}.
	 *
	 * @return the parent {@link Series}
	 */
	public Series getSeries() {
		return series;
	}

	/**
	 * Sets the parent {@link Series}.
	 *
	 * @param series the new parent {@link Series}
	 */
	public void setSeries(Series series) {
		this.series = series;
	}

	/**
	 * Gets the parent {@link Album}.
	 *
	 * @return the parent {@link Album}
	 */
	public Album getAlbum() {
		return album;
	}

	/**
	 * Sets the parent {@link Album}.
	 *
	 * @param album the new parent {@link Album}
	 */
	public void setAlbum(Album album) {
		this.album = album;
	}

	/**
	 * Gets the {@link Author} who worked on this Derivative.
	 *
	 * @return the {@link Author} who worked on this Derivative
	 */
	public Author getArtist() {
		return artist;
	}

	/**
	 * Sets the {@link Author} who worked on this Derivative.
	 *
	 * @param artist the new {@link Author} who worked on this Derivative
	 */
	public void setArtist(Author artist) {
		this.artist = artist;
	}

	/**
	 * Gets the {@link DerivativeType type} of this Derivative.
	 *
	 * @return the {@link DerivativeType type} of this Derivative
	 */
	public DerivativeType getType() {
		return type;
	}

	/**
	 * Sets the {@link DerivativeType type} of this Derivative.
	 *
	 * @param type the new {@link DerivativeType type} of this Derivative
	 */
	public void setType(DerivativeType type) {
		this.type = type;
	}

	/**
	 * Gets the number of colours.
	 *
	 * @return the number of colours
	 */
	public Integer getColours() {
		return colours;
	}

	/**
	 * Sets the number of colours.
	 *
	 * @param colours the new number of colours
	 */
	public void setColours(Integer colours) {
		this.colours = colours;
	}

	/**
	 * Gets the {@link DerivativeSource source} of this Derivative.
	 *
	 * @return the {@link DerivativeSource source} of this Derivative
	 */
	public DerivativeSource getSource() {
		return source;
	}

	/**
	 * Sets the {@link DerivativeSource source} of this Derivative.
	 *
	 * @param source the new {@link DerivativeSource source} of this Derivative
	 */
	public void setSource(DerivativeSource source) {
		this.source = source;
	}

	/**
	 * Gets the item number.
	 *
	 * @return the item number
	 */
	public Integer getNumber() {
		return number;
	}

	/**
	 * Sets the item number.
	 *
	 * @param number the new item number
	 */
	public void setNumber(Integer number) {
		this.number = number;
	}

	/**
	 * Gets the total count of items.
	 *
	 * @return the total count of items
	 */
	public Integer getTotal() {
		return total;
	}

	/**
	 * Sets the total count of items.
	 *
	 * @param total the new total count of items
	 */
	public void setTotal(Integer total) {
		this.total = total;
	}

	/**
	 * Checks if is the flag indicating whether this Derivative is signed by its artist.
	 *
	 * @return the flag indicating whether this Derivative is signed by its artist
	 */
	public boolean isSigned() {
		return signed;
	}

	/**
	 * Sets the flag indicating whether this Derivative is signed by its artist.
	 *
	 * @param signed the new flag indicating whether this Derivative is signed by its artist
	 */
	public void setSigned(boolean signed) {
		this.signed = signed;
	}

	/**
	 * Checks if is the flag indicating whether this Derivative is an artist's personal copy.
	 *
	 * @return the flag indicating whether this Derivative is an artist's personal copy
	 */
	public boolean isAuthorsCopy() {
		return authorsCopy;
	}

	/**
	 * Sets the flag indicating whether this Derivative is an artist's personal copy.
	 *
	 * @param authorsCopy the new flag indicating whether this Derivative is an artist's personal copy
	 */
	public void setAuthorsCopy(boolean authorsCopy) {
		this.authorsCopy = authorsCopy;
	}

	/**
	 * Checks if is the flag indicating whether this Derivative is marked as restricted sale.
	 *
	 * @return the flag indicating whether this Derivative is marked as restricted sale
	 */
	public boolean isRestrictedSale() {
		return restrictedSale;
	}

	/**
	 * Sets the flag indicating whether this Derivative is marked as restricted sale.
	 *
	 * @param restrictedSale the new flag indicating whether this Derivative is marked as restricted sale
	 */
	public void setRestrictedSale(boolean restrictedSale) {
		this.restrictedSale = restrictedSale;
	}

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
	 * @param description the new description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the height of the Album.
	 *
	 * @return the height of the Album
	 */
	public BigDecimal getHeight() {
		return height;
	}

	/**
	 * Sets the height of the Album.
	 *
	 * @param height the new height of the Album
	 */
	public void setHeight(BigDecimal height) {
		this.height = height;
	}

	/**
	 * Gets the width of the Album.
	 *
	 * @return the width of the Album
	 */
	public BigDecimal getWidth() {
		return width;
	}

	/**
	 * Sets the width of the Album.
	 *
	 * @param width the new width of the Album
	 */
	public void setWidth(BigDecimal width) {
		this.width = width;
	}

	/**
	 * Gets the width of the Album.
	 *
	 * @return the width of the Album
	 */
	public BigDecimal getDepth() {
		return depth;
	}

	/**
	 * Sets the width of the Album.
	 *
	 * @param depth the new width of the Album
	 */
	public void setDepth(BigDecimal depth) {
		this.depth = depth;
	}

	@Override
	public SortedSet<DerivativePrice> getPrices() {
		return prices;
	}

	@Override
	protected void setPricesRaw(SortedSet<DerivativePrice> pricesArg) {
		this.prices = pricesArg;
	}

	@Override
	@Transient
	// Must override so that Spring know the concrete type
	public List<DerivativePrice> getPriceList() {
		return super.getPriceList();
	}

	/**
	 * Gets the {@link Image}s related to this Derivative.
	 *
	 * @return the {@link Image}s related to this Derivative
	 */
	public SortedSet<Image> getImages() {
		return images;
	}

	/**
	 * Sets the {@link Image}s related to this Derivative.
	 *
	 * @param images the new {@link Image}s related to this Derivative
	 */
	public void setImages(SortedSet<Image> images) {
		this.images = images;
	}
}
