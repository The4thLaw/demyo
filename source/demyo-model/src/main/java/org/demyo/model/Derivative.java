/*
 * 
 */
package org.demyo.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.demyo.model.util.DefaultOrder;

/**
 * Derivatives of {@link Album}s or {@link Series}.
 */
// TODO: validation rule for either an Album or a Series at least
@Entity
@Table(name = "DERIVATIVES")
@DefaultOrder(expression = { @DefaultOrder.Order(property = "series.name"),
		@DefaultOrder.Order(property = "album.cycle"), @DefaultOrder.Order(property = "album.number"),
		@DefaultOrder.Order(property = "album.numberSuffix"), @DefaultOrder.Order(property = "album.title") })
@NamedEntityGraphs({
		@NamedEntityGraph(name = "Derivative.forIndex", attributeNodes = { @NamedAttributeNode("artist"),
				@NamedAttributeNode("images") }),
		@NamedEntityGraph(name = "Derivative.forEdition", attributeNodes = { @NamedAttributeNode("artist"),
				@NamedAttributeNode("images") }) })
// TODO: Add constraint to have either Album or Series nullable, but not both
public class Derivative extends AbstractModel {
	/** The parent {@link Series}. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "series_id")
	private Series series;

	/** The parent {@link Album}. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "album_id")
	private Album album;

	/** The {@link Author} who worked on this Derivative. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "artist_id")
	private Author artist;

	/** The {@link DerivativeType type} of this Derivative. */
	@NotNull
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "derivative_type_id")
	private DerivativeType type;

	/** The number of colours. */
	@Column(name = "colors")
	private Integer colours;

	/** The {@link DerivativeSource source} of this Derivative. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "source_id")
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

	/** The date of acquisition. */
	@Column(name = "acquisition_date")
	private Date acquisitionDate;

	/** The price the album was purchased for. */
	@Column(name = "purchase_price")
	@Min(0)
	private BigDecimal purchasePrice;

	/** The {@link Image}s related to this Derivative. */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "derivatives_images", joinColumns = @JoinColumn(name = "derivative_id"),
			inverseJoinColumns = @JoinColumn(name = "image_id"))
	private Set<Image> images;

	@Override
	public String getIdentifyingName() {
		StringBuilder sb = new StringBuilder();
		if (series != null) {
			sb.append(series.getIdentifyingName()).append(" - ");
		}
		if (album != null) {
			sb.append(album.getIdentifyingName()).append(" - ");
		}
		sb.append(type.getIdentifyingName());
		if (source != null) {
			sb.append(" ").append(source.getIdentifyingName());
		}
		return sb.toString();
	}

	public Image getMainImage() {
		if (images.size() == 0) {
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
	 * Gets the {@link Author} who wworked on this Derivative.
	 * 
	 * @return the {@link Author} who wworked on this Derivative
	 */
	public Author getArtist() {
		return artist;
	}

	/**
	 * Sets the {@link Author} who wworked on this Derivative.
	 * 
	 * @param artist the new {@link Author} who wworked on this Derivative
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

	/**
	 * Gets the date of acquisition.
	 * 
	 * @return the date of acquisition
	 */
	public Date getAcquisitionDate() {
		return acquisitionDate;
	}

	/**
	 * Sets the date of acquisition.
	 * 
	 * @param acquisitionDate the new date of acquisition
	 */
	public void setAcquisitionDate(Date acquisitionDate) {
		this.acquisitionDate = acquisitionDate;
	}

	/**
	 * Gets the price the album was purchased for.
	 * 
	 * @return the price the album was purchased for
	 */
	public BigDecimal getPurchasePrice() {
		return purchasePrice;
	}

	/**
	 * Sets the price the album was purchased for.
	 * 
	 * @param purchasePrice the new price the album was purchased for
	 */
	public void setPurchasePrice(BigDecimal purchasePrice) {
		this.purchasePrice = purchasePrice;
	}

	/**
	 * Gets the {@link Image}s related to this Derivative.
	 * 
	 * @return the {@link Image}s related to this Derivative
	 */
	public Set<Image> getImages() {
		return images;
	}

	/**
	 * Sets the {@link Image}s related to this Derivative.
	 * 
	 * @param images the new {@link Image}s related to this Derivative
	 */
	public void setImages(Set<Image> images) {
		this.images = images;
	}
}
