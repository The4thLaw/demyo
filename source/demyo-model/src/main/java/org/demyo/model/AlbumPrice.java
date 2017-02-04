package org.demyo.model;

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

import org.demyo.model.util.AbstractModelComparator;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Defines a dated price for an {@link Album}.
 */
@Entity
@Table(name = "ALBUMS_PRICES")
@IdClass(AlbumPriceId.class)
// TODO [P1]: consider refactoring to share some aspects with the DerivativePrice
public class AlbumPrice implements Comparable<AlbumPrice> {
	/** The parent {@link Album} ID. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "album_id")
	@NotNull
	@Id
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
		int comparison = 0;

		if (album != null) {
			comparison = AbstractModelComparator.nullSafeComparison(album.getId(), o.album.getId());
		}
		if (comparison != 0) {
			return comparison;
		}

		comparison = AbstractModelComparator.nullSafeComparison(date, o.date);
		if (comparison != 0) {
			return comparison;
		}

		comparison = AbstractModelComparator.nullSafeComparison(price, o.price);

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
		return "AlbumPrice(album_id=" + (album != null ? album.getId() : null) + ", date=" + date + ", price="
				+ price + ")";
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof AlbumPrice)) {
			return false;
		}
		AlbumPrice otherPrice = (AlbumPrice) other;
		EqualsBuilder builder = new EqualsBuilder();
		builder.append(album, otherPrice.album);
		builder.append(date, otherPrice.date);
		builder.append(price, otherPrice.price);
		return builder.build();
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		builder.append(album);
		builder.append(date);
		builder.append(price);
		return builder.build();
	}
}
