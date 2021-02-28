package org.demyo.model;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.SortComparator;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.querydsl.core.annotations.PropertyType;
import com.querydsl.core.annotations.QueryType;

import org.demyo.model.constraints.ISBN;
import org.demyo.model.jackson.SortedSetDeserializer;
import org.demyo.model.util.AuthorComparator;
import org.demyo.model.util.ComparableComparator;
import org.demyo.model.util.DefaultOrder;
import org.demyo.model.util.IdentifyingNameComparator;

/**
 * Represents Albums. Albums are the core elements in Demyo. They may belong to a {@link Series} or not.
 */
@Entity
@Table(name = "ALBUMS")
@DefaultOrder(expression =
{ @DefaultOrder.Order(property = "series.name"), @DefaultOrder.Order(property = "cycle"),
		@DefaultOrder.Order(property = "number"), @DefaultOrder.Order(property = "numberSuffix"),
		// All other things being equal, sort by title. It's more intuitive for one shots in Album indexes, for example
		@DefaultOrder.Order(property = "title") })
@NamedEntityGraph(name = "Album.forIndex", attributeNodes =
{ @NamedAttributeNode("series") })
@NamedEntityGraph(name = "Album.forView", attributeNodes =
{ @NamedAttributeNode("series"), @NamedAttributeNode("publisher"), @NamedAttributeNode("collection"),
		@NamedAttributeNode("cover"), @NamedAttributeNode("binding"), @NamedAttributeNode("tags"),
		@NamedAttributeNode("writers"), @NamedAttributeNode("artists"), @NamedAttributeNode("colorists"),
		@NamedAttributeNode("inkers"), @NamedAttributeNode("translators"), @NamedAttributeNode("images"),
		@NamedAttributeNode("prices"), @NamedAttributeNode("readersFavourites") })
@NamedEntityGraph(name = "Album.forEdition", attributeNodes =
{ @NamedAttributeNode("series"), @NamedAttributeNode("publisher"), @NamedAttributeNode("collection"),
		@NamedAttributeNode("cover"), @NamedAttributeNode("binding"), @NamedAttributeNode("tags"),
		@NamedAttributeNode("writers"), @NamedAttributeNode("artists"), @NamedAttributeNode("colorists"),
		@NamedAttributeNode("inkers"), @NamedAttributeNode("translators"), @NamedAttributeNode("images"),
		@NamedAttributeNode("prices") })
@JsonView(ModelView.AlbumTemplate.class)
public class Album extends AbstractPricedModel<AlbumPrice, Album> {
	/** The parent {@link Series}. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "series_id")
	@JsonView(ModelView.Basic.class)
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
	@JsonView(ModelView.Basic.class)
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

	/** The date of printing for this edition. Usually equal to the current edition, plus or minus 1 month. */
	@Column(name = "printing")
	private Date printingDate;

	/** The flag indicating the Album was explicitly marked as being a first edition. */
	@Column(name = "marked_as_first_edition")
	private boolean markedAsFirstEdition;

	/** The ISBN. */
	@Column(name = "isbn")
	@ISBN
	private String isbn;

	/** The prices applicable to the Album. */
	@OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
	// Not insertable or updatable: managed by the child entity
	@JoinColumn(name = "album_id", insertable = false, updatable = false)
	@SortComparator(ComparableComparator.class)
	@Valid
	@JsonManagedReference
	private SortedSet<AlbumPrice> prices;

	/** The flag indicating whether an item is part of the wishlist. */
	@Column(name = "wishlist")
	@JsonView(ModelView.Basic.class)
	private boolean wishlist;

	/** The physical location of this Album. */
	@Column(name = "location")
	private String location;

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

	/** The summary of the Album. */
	@Column(name = "summary")
	private String summary;

	/** The comments from the user. */
	@Column(name = "comment")
	private String comment;

	/** The {@link Tag}s labeling this Album. */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "albums_tags", joinColumns = @JoinColumn(name = "album_id"), //
			inverseJoinColumns = @JoinColumn(name = "tag_id"))
	@SortComparator(IdentifyingNameComparator.class)
	@JsonDeserialize(using = SortedSetDeserializer.class)
	private SortedSet<Tag> tags;

	/** The {@link Author}s who wrote this Album. */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "albums_writers", joinColumns = @JoinColumn(name = "album_id"), //
			inverseJoinColumns = @JoinColumn(name = "writer_id"))
	@SortComparator(AuthorComparator.class)
	@JsonDeserialize(using = SortedSetDeserializer.class)
	private SortedSet<Author> writers;

	/** The {@link Author}s who drew this Album. */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "albums_artists", joinColumns = @JoinColumn(name = "album_id"), //
			inverseJoinColumns = @JoinColumn(name = "artist_id"))
	@SortComparator(AuthorComparator.class)
	@JsonDeserialize(using = SortedSetDeserializer.class)
	private SortedSet<Author> artists;

	/** The {@link Author}s who colored this Album. */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "albums_colorists", joinColumns = @JoinColumn(name = "album_id"), //
			inverseJoinColumns = @JoinColumn(name = "colorist_id"))
	@SortComparator(AuthorComparator.class)
	@JsonDeserialize(using = SortedSetDeserializer.class)
	private SortedSet<Author> colorists;

	/** The {@link Author}s who inked this Album. */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "albums_inkers", joinColumns = @JoinColumn(name = "album_id"), //
			inverseJoinColumns = @JoinColumn(name = "inker_id"))
	@SortComparator(AuthorComparator.class)
	@JsonDeserialize(using = SortedSetDeserializer.class)
	private SortedSet<Author> inkers;

	/** The {@link Author}s who translated this Album. */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "albums_translators", joinColumns = @JoinColumn(name = "album_id"), //
			inverseJoinColumns = @JoinColumn(name = "translator_id"))
	@SortComparator(AuthorComparator.class)
	@JsonDeserialize(using = SortedSetDeserializer.class)
	private SortedSet<Author> translators;

	/** The {@link Image}s related to this Album. */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "albums_images", joinColumns = @JoinColumn(name = "album_id"), //
			inverseJoinColumns = @JoinColumn(name = "image_id"))
	@BatchSize(size = BATCH_SIZE)
	@SortComparator(IdentifyingNameComparator.class)
	@JsonDeserialize(using = SortedSetDeserializer.class)
	private SortedSet<Image> images;

	/** The {@link Reader}s who favourited this Album. */
	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "favouriteAlbums")
	@SortComparator(IdentifyingNameComparator.class)
	@JsonDeserialize(using = SortedSetDeserializer.class)
	private SortedSet<Reader> readersFavourites;

	/** The {@link Reader}s who have this Album in their reading list. */
	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "readingList")
	@SortComparator(IdentifyingNameComparator.class)
	@JsonDeserialize(using = SortedSetDeserializer.class)
	private SortedSet<Reader> readersReadingList;

	@Override
	protected Album self() {
		return this;
	}

	@Override
	public String getIdentifyingName() {
		StringBuilder sb = getQualifiedNumber();

		if (sb.length() > 0) {
			sb.append(" - ");
		}
		if (title != null) {
			// Can happen in some edge cases, like album templates
			sb.append(title);
		}

		return sb.toString();
	}

	/**
	 * Returns a formatted version of the combined cycle, number and suffix.
	 * 
	 * @return A working StringBuilder. Never <code>null</code>.
	 */
	@QueryType(PropertyType.NONE)
	private StringBuilder getQualifiedNumber() {
		StringBuilder sb = new StringBuilder();

		if (cycle != null) {
			sb.append(cycle);
		}
		if (number != null) {
			if (sb.length() > 0) {
				sb.append(".");
			}
			sb.append(getNumberFormat().format(number.doubleValue()));
		}
		if (numberSuffix != null) {
			if (sb.length() > 0) {
				sb.append(" ");
			}
			sb.append(numberSuffix);
		}
		return sb;
	}

	/**
	 * Returns the base name that can be used for automatic image descriptions.
	 * 
	 * @return The name.
	 */
	public String getBaseNameForImages() {
		if (series == null) {
			return title;
		}

		StringBuilder sb = new StringBuilder();

		sb.append(series.getIdentifyingName())//
				.append(" ")//
				.append(getQualifiedNumber());

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
	 * Sets the date of printing for this edition.
	 *
	 * @param printingDate the new date of printing for this edition
	 */
	public void setPrintingDate(Date printingDate) {
		this.printingDate = printingDate;
	}

	/**
	 * Gets the date of printing for this edition.
	 *
	 * @return the date of printing for this edition
	 */
	public Date getPrintingDate() {
		return printingDate;
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
	 * Checks the flag indicating the Album was explicitly marked as being a first edition.
	 *
	 * @return the flag indicating the Album was explicitly marked as being a first edition
	 */
	public boolean isMarkedAsFirstEdition() {
		return markedAsFirstEdition;
	}

	/**
	 * Sets the flag indicating the Album was explicitly marked as being a first edition.
	 *
	 * @param markedAsFirstEdition the new flag indicating the Album was explicitly marked as being a first edition
	 */
	public void setMarkedAsFirstEdition(boolean markedAsFirstEdition) {
		this.markedAsFirstEdition = markedAsFirstEdition;
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

	@Override
	public SortedSet<AlbumPrice> getPrices() {
		return prices;
	}

	@Override
	protected void setPricesRaw(SortedSet<AlbumPrice> pricesArg) {
		this.prices = pricesArg;
	}

	@Override
	@Transient
	// Must override so that Spring know the concrete type
	public List<AlbumPrice> getPriceList() {
		return super.getPriceList();
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
	 * Gets the physical location of this Album.
	 * 
	 * @return the physical location of this Album
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * Gets the physical location of this Album, potentially guessing it from the Series.
	 * 
	 * @return If the Album has a location, it is returned. Else, if the Album belongs to a Series and that Series has a
	 *         location, it is returned. Else, <code>null</code> is returned.
	 */
	public String getAggregatedLocation() {
		if (location != null) {
			return location;
		}
		if (series != null) {
			return series.getLocation();
		}
		return null;
	}

	/**
	 * Sets the physical location of this Album.
	 * 
	 * @param location the new physical location of this Album
	 */
	public void setLocation(String location) {
		this.location = location;
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
	 * Gets the {@link Author}s who inked this Album.
	 * 
	 * @return the {@link Author}s who inked this Album
	 */
	public SortedSet<Author> getInkers() {
		return inkers;
	}

	/**
	 * Sets the {@link Author}s who inked this Album.
	 * 
	 * @param inkers the new {@link Author}s who inked this Album
	 */
	public void setInkers(SortedSet<Author> inkers) {
		this.inkers = inkers;
	}

	/**
	 * Gets the {@link Author}s who translated this Album.
	 * 
	 * @return the {@link Author}s who translated this Album
	 */
	public SortedSet<Author> getTranslators() {
		return translators;
	}

	/**
	 * Sets the {@link Author}s who translated this Album.
	 * 
	 * @param translators the new {@link Author}s who translated this Album
	 */
	public void setTranslators(SortedSet<Author> translators) {
		this.translators = translators;
	}

	/**
	 * Gets the {@link Image}s related to this Album.
	 * 
	 * @return the {@link Image}s related to this Album
	 */
	public SortedSet<Image> getImages() {
		return images;
	}

	/**
	 * Sets the {@link Image}s related to this Album.
	 * 
	 * @param images the new {@link Image}s related to this Album
	 */
	public void setImages(SortedSet<Image> images) {
		this.images = images;
	}

	/**
	 * Gets a NumberFormat for use in {@link #getQualifiedNumber()}.
	 * <p>
	 * Done as an instance method rather than a ThreadLocal to avoid potential leaks. However, the number of times we
	 * may call this method doesn't warrant the inception of a more convoluted ThreadLocal recycler.
	 * </p>
	 * 
	 * @return The number format.
	 */
	private static NumberFormat getNumberFormat() {
		NumberFormat fmt = NumberFormat.getInstance();
		if (fmt instanceof DecimalFormat) {
			DecimalFormat df = (DecimalFormat) fmt;
			DecimalFormatSymbols symbols = (DecimalFormatSymbols) df.getDecimalFormatSymbols().clone();
			symbols.setDecimalSeparator('.'); // We always want a dot in this case
			df.setDecimalFormatSymbols(symbols);
			df.applyPattern("#0.#");
		}
		return fmt;
	}
}
