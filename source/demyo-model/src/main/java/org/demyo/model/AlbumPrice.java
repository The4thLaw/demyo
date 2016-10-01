package org.demyo.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.demyo.model.AlbumPrice.AlbumPriceId;

/**
 * Defines a dated price for an {@link Album}.
 */
@Entity
@Table(name = "ALBUMS_PRICES")
@IdClass(AlbumPriceId.class)
public class AlbumPrice implements Comparable<AlbumPrice> {
	/**
	 * Embedded class for the ID.
	 */
	public static class AlbumPriceId implements Serializable {
		private static final long serialVersionUID = -8902912798213320698L;

		private Long album;
		private Date date;

		/**
		 * Default constructor.
		 */
		public AlbumPriceId() {

		}

		/**
		 * Creates an ID based on the provided fields.
		 * 
		 * @param album The Album ID
		 * @param date The date for the price
		 */
		public AlbumPriceId(Long album, Date date) {
			this.album = album;
			this.date = date;
		}

		@Override
		public boolean equals(Object other) {
			if (!(other instanceof AlbumPriceId)) {
				return false;
			}
			AlbumPriceId otherId = (AlbumPriceId) other;
			return album.equals(otherId.album) && date.equals(otherId.date);
		}

		@Override
		public int hashCode() {
			return album.hashCode() ^ date.hashCode();
		}
	}

	/** The parent {@link Album}. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "album_id")
	@Id
	@NotNull
	private Album album;

	/** The date at which the price was applicable. */
	@Column(name = "date")
	@Id
	@NotNull
	private Date date;

	/** The price the album was worth at the given date. */
	@Column(name = "price")
	@Min(0)
	@NotNull
	private BigDecimal price;

	@Override
	public int compareTo(AlbumPrice o) {
		int comparison;

		comparison = album.getId().compareTo(o.album.getId());
		if (comparison != 0) {
			return comparison;
		}

		comparison = date.compareTo(o.date);
		if (comparison != 0) {
			return comparison;
		}

		comparison = price.compareTo(o.price);

		return comparison;
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
	 * Gets the date at which the price was applicable.
	 * 
	 * @return the date at which the price was applicable
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * Sets the date at which the price was applicable.
	 * 
	 * @param date the new date at which the price was applicable
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * Gets the price the album was worth at the given date.
	 * 
	 * @return the price the album was worth at the given date
	 */
	public BigDecimal getPrice() {
		return price;
	}

	/**
	 * Sets the price the album was worth at the given date.
	 * 
	 * @param price the new price the album was worth at the given date
	 */
	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	@Override
	public String toString() {
		return "AlbumPrice(album_id=" + album.getId() + ", date=" + date + ", price=" + price + ")";
	}
}
