package org.demyo.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.demyo.model.AlbumPrice.AlbumPriceId;
import org.demyo.model.util.AbstractModelComparator;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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

		private Long albumId;
		private Date date;

		/**
		 * Default constructor.
		 */
		public AlbumPriceId() {

		}

		/**
		 * Creates an ID based on the provided fields.
		 * 
		 * @param albumId The Album ID
		 * @param date The date for the price
		 */
		public AlbumPriceId(Long albumId, Date date) {
			this.albumId = albumId;
			this.date = date;
		}

		@Override
		public boolean equals(Object other) {
			if (!(other instanceof AlbumPriceId)) {
				return false;
			}
			AlbumPriceId otherId = (AlbumPriceId) other;
			return albumId.equals(otherId.albumId) && date.equals(otherId.date);
		}

		@Override
		public int hashCode() {
			return albumId.hashCode() ^ date.hashCode();
		}

		@Override
		public String toString() {
			return "AlbumPriceId(album_id=" + albumId + ", date=" + date + ")";
		}
	}

	/** The parent {@link Album} ID. */
	@Column(name = "album_id")
	@Id
	@NotNull
	private Long albumId;

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

		comparison = AbstractModelComparator.nullSafeComparison(albumId, o.albumId);
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
	 * Gets the parent {@link Album} ID.
	 * 
	 * @return the parent {@link Album} ID
	 */
	public Long getAlbumId() {
		return albumId;
	}

	/**
	 * Sets the parent {@link Album} ID.
	 * 
	 * @param albumId the new parent {@link Album} ID
	 */
	public void setAlbumId(Long albumId) {
		this.albumId = albumId;
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
		return "AlbumPrice(album_id=" + albumId + ", date=" + date + ", price=" + price + ")";
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof AlbumPrice)) {
			return false;
		}
		AlbumPrice otherPrice = (AlbumPrice) other;
		EqualsBuilder builder = new EqualsBuilder();
		builder.append(albumId, otherPrice.albumId);
		builder.append(date, otherPrice.date);
		builder.append(price, otherPrice.price);
		return builder.build();
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		builder.append(albumId);
		builder.append(date);
		builder.append(price);
		return builder.build();
	}
}
