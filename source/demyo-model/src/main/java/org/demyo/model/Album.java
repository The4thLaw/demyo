package org.demyo.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;
import java.util.SortedSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.demyo.model.constraints.ISBN;

import org.hibernate.validator.constraints.NotBlank;

/**
 * The Class Album.
 */
@Entity
@Table(name = "ALBUMS")
@NamedEntityGraph(name = "Album.forEdition", attributeNodes = { @NamedAttributeNode("series"),
		@NamedAttributeNode("publisher"), @NamedAttributeNode("collection"), @NamedAttributeNode("cover") })
// TODO: prices
// TODO: loans
public class Album extends AbstractModel {
	/** The parent {@link Series}. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "series_id")
	private Series series;

	/** The cycle. */
	@Column(name = "cycle")
	private Integer cycle;

	/** The number. */
	@Column(name = "number")
	private BigDecimal number;

	/** The number suffix. */
	@Column(name = "number_suffix")
	private String numberSuffix;

	/** The title. */
	@Column(name = "title")
	@NotBlank
	private String title;

	/** The owning {@link Publisher}. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "publisher_id")
	@NotNull
	private Publisher publisher;

	/** The owning {@link Collection}. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "collection_id")
	private Collection collection;

	/** The date of first edition. */
	@Column(name = "first_edition")
	private Date firstEditionDate;

	/** The date of current edition. */
	@Column(name = "this_edition")
	private Date currentEditionDate;

	/** The ISBN. */
	@Column(name = "isbn")
	@ISBN
	private String isbn;

	/** The date of acquisition. */
	@Column(name = "acquisition_date")
	private Date acquisitionDate;

	/** The price the album was purchased for. */
	@Column(name = "purchase_price")
	@Min(0)
	private BigDecimal purchasePrice;

	/** The flag indicating whether an item is part of the wishlist. */
	@Column(name = "wishlist")
	private boolean wishlist;

	/** The {@link Binding} for this Album. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "binding_id")
	private Binding binding;

	/** The height of the Album. */
	@Column(name = "height")
	@Min(0)
	private BigDecimal height;

	/** The width of the Album. */
	@Column(name = "width")
	@Min(0)
	private BigDecimal width;

	/** The number of pages. */
	@Column(name = "pages")
	@Min(0)
	private Integer pages;

	/** The album cover. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cover_id")
	private Image cover;

	/** The summary of the album. */
	@Column(name = "summary")
	private String summary;

	/** The comments from the user. */
	@Column(name = "comment")
	private String comment;

	/** The {@link Tag}s labelling this Album. */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "albums_tags", joinColumns = @JoinColumn(name = "album_id"),
			inverseJoinColumns = @JoinColumn(name = "tag_id"))
	@OrderBy(value = "name asc")
	private SortedSet<Tag> tags;

	/** The {@link Author}s who wrote this Album. */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "albums_writers", joinColumns = @JoinColumn(name = "album_id"),
			inverseJoinColumns = @JoinColumn(name = "writer_id"))
	@OrderBy(value = "name asc, fname asc")
	private SortedSet<Author> writers;

	/** The {@link Author}s who drew this Album. */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "albums_artists", joinColumns = @JoinColumn(name = "album_id"),
			inverseJoinColumns = @JoinColumn(name = "artist_id"))
	@OrderBy(value = "name asc, fname asc")
	private SortedSet<Author> artists;

	/** The {@link Author}s who colored this Album. */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "albums_colorists", joinColumns = @JoinColumn(name = "album_id"),
			inverseJoinColumns = @JoinColumn(name = "colorist_id"))
	@OrderBy(value = "name asc, fname asc")
	private SortedSet<Author> colorists;

	/** The {@link Image}s related to this Album. */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "albums_images", joinColumns = @JoinColumn(name = "album_id"),
			inverseJoinColumns = @JoinColumn(name = "image_id"))
	private Set<Image> images;

	@Override
	public String getIdentifyingName() {
		StringBuilder sb = new StringBuilder();

		if (cycle != null) {
			sb.append(cycle);
		}
		if (number != null) {
			if (sb.length() > 0) {
				sb.append(".");
			}
			// TODO: investigate other ways to round a decimal if it has no actual decimals
			if (number.compareTo(BigDecimal.valueOf(number.intValue())) == 0) {
				sb.append(number.intValueExact());
			} else {
				sb.append(number);
			}
		}
		if (numberSuffix != null) {
			if (sb.length() > 0) {
				sb.append(" - ");
			}
			sb.append(numberSuffix);
		}

		if (sb.length() > 0) {
			sb.append(" ");
		}
		sb.append(title);

		return sb.toString();
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
	 * Gets the cycle.
	 * 
	 * @return the cycle
	 */
	public Integer getCycle() {
		return cycle;
	}

	/**
	 * Sets the cycle.
	 * 
	 * @param cycle the new cycle
	 */
	public void setCycle(Integer cycle) {
		this.cycle = cycle;
	}

	/**
	 * Gets the number.
	 * 
	 * @return the number
	 */
	public BigDecimal getNumber() {
		return number;
	}

	/**
	 * Sets the number.
	 * 
	 * @param number the new number
	 */
	public void setNumber(BigDecimal number) {
		this.number = number;
	}

	/**
	 * Gets the number suffix.
	 * 
	 * @return the number suffix
	 */
	public String getNumberSuffix() {
		return numberSuffix;
	}

	/**
	 * Sets the number suffix.
	 * 
	 * @param numberSuffix the new number suffix
	 */
	public void setNumberSuffix(String numberSuffix) {
		this.numberSuffix = numberSuffix;
	}

	/**
	 * Gets the title.
	 * 
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the title.
	 * 
	 * @param title the new title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Gets the owning {@link Publisher}.
	 * 
	 * @return the owning {@link Publisher}
	 */
	public Publisher getPublisher() {
		return publisher;
	}

	/**
	 * Sets the owning {@link Publisher}.
	 * 
	 * @param publisher the new owning {@link Publisher}
	 */
	public void setPublisher(Publisher publisher) {
		this.publisher = publisher;
	}

	/**
	 * Gets the owning {@link Collection}.
	 * 
	 * @return the owning {@link Collection}
	 */
	public Collection getCollection() {
		return collection;
	}

	/**
	 * Sets the owning {@link Collection}.
	 * 
	 * @param collection the new owning {@link Collection}
	 */
	public void setCollection(Collection collection) {
		this.collection = collection;
	}

	/**
	 * Gets the date of first edition.
	 * 
	 * @return the date of first edition
	 */
	public Date getFirstEditionDate() {
		return firstEditionDate;
	}

	/**
	 * Sets the date of first edition.
	 * 
	 * @param firstEditionDate the new date of first edition
	 */
	public void setFirstEditionDate(Date firstEditionDate) {
		this.firstEditionDate = firstEditionDate;
	}

	/**
	 * Gets the date of current edition.
	 * 
	 * @return the date of current edition
	 */
	public Date getCurrentEditionDate() {
		return currentEditionDate;
	}

	/**
	 * Sets the date of current edition.
	 * 
	 * @param currentEditionDate the new date of current edition
	 */
	public void setCurrentEditionDate(Date currentEditionDate) {
		this.currentEditionDate = currentEditionDate;
	}

	/**
	 * Gets the ISBN.
	 * 
	 * @return the ISBN
	 */
	public String getIsbn() {
		return isbn;
	}

	/**
	 * Sets the ISBN.
	 * 
	 * @param isbn the new ISBN
	 */
	public void setIsbn(String isbn) {
		this.isbn = isbn;
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
	 * Checks if is the flag indicating whether an item is part of the wishlist.
	 * 
	 * @return the flag indicating whether an item is part of the wishlist
	 */
	public boolean isWishlist() {
		return wishlist;
	}

	/**
	 * Sets the flag indicating whether an item is part of the wishlist.
	 * 
	 * @param wishlist the new flag indicating whether an item is part of the wishlist
	 */
	public void setWishlist(boolean wishlist) {
		this.wishlist = wishlist;
	}

	/**
	 * Gets the {@link Binding} for this Album.
	 * 
	 * @return the {@link Binding} for this Album
	 */
	public Binding getBinding() {
		return binding;
	}

	/**
	 * Sets the {@link Binding} for this Album.
	 * 
	 * @param binding the new {@link Binding} for this Album
	 */
	public void setBinding(Binding binding) {
		this.binding = binding;
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
	 * Gets the number of pages.
	 * 
	 * @return the number of pages
	 */
	public Integer getPages() {
		return pages;
	}

	/**
	 * Sets the number of pages.
	 * 
	 * @param pages the new number of pages
	 */
	public void setPages(Integer pages) {
		this.pages = pages;
	}

	/**
	 * Gets the album cover.
	 * 
	 * @return the album cover
	 */
	public Image getCover() {
		return cover;
	}

	/**
	 * Sets the album cover.
	 * 
	 * @param cover the new album cover
	 */
	public void setCover(Image cover) {
		this.cover = cover;
	}

	/**
	 * Gets the summary of the album.
	 * 
	 * @return the summary of the album
	 */
	public String getSummary() {
		return summary;
	}

	/**
	 * Sets the summary of the album.
	 * 
	 * @param summary the new summary of the album
	 */
	public void setSummary(String summary) {
		this.summary = summary;
	}

	/**
	 * Gets the comments from the user.
	 * 
	 * @return the comments from the user
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * Sets the comments from the user.
	 * 
	 * @param comment the new comments from the user
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * Gets the {@link Tag}s labelling this Album.
	 * 
	 * @return the {@link Tag}s labelling this Album
	 */
	public SortedSet<Tag> getTags() {
		return tags;
	}

	/**
	 * Sets the {@link Tag}s labelling this Album.
	 * 
	 * @param tags the new {@link Tag}s labelling this Album
	 */
	public void setTags(SortedSet<Tag> tags) {
		this.tags = tags;
	}

	/**
	 * Gets the {@link Author}s who wrote this Album.
	 * 
	 * @return the {@link Author}s who wrote this Album
	 */
	public SortedSet<Author> getWriters() {
		return writers;
	}

	/**
	 * Sets the {@link Author}s who wrote this Album.
	 * 
	 * @param writers the new {@link Author}s who wrote this Album
	 */
	public void setWriters(SortedSet<Author> writers) {
		this.writers = writers;
	}

	/**
	 * Gets the {@link Author}s who drew this Album.
	 * 
	 * @return the {@link Author}s who drew this Album
	 */
	public SortedSet<Author> getArtists() {
		return artists;
	}

	/**
	 * Sets the {@link Author}s who drew this Album.
	 * 
	 * @param artists the new {@link Author}s who drew this Album
	 */
	public void setArtists(SortedSet<Author> artists) {
		this.artists = artists;
	}

	/**
	 * Gets the {@link Author}s who colored this Album.
	 * 
	 * @return the {@link Author}s who colored this Album
	 */
	public SortedSet<Author> getColorists() {
		return colorists;
	}

	/**
	 * Sets the {@link Author}s who colored this Album.
	 * 
	 * @param colorists the new {@link Author}s who colored this Album
	 */
	public void setColorists(SortedSet<Author> colorists) {
		this.colorists = colorists;
	}

	/**
	 * Gets the {@link Image}s related to this Album.
	 * 
	 * @return the {@link Image}s related to this Album
	 */
	public Set<Image> getImages() {
		return images;
	}

	/**
	 * Sets the {@link Image}s related to this Album.
	 * 
	 * @param images the new {@link Image}s related to this Album
	 */
	public void setImages(Set<Image> images) {
		this.images = images;
	}
}
