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

import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * Defines a dated price for an {@link Album}.
 */
@Entity
@Table(name = "ALBUMS_PRICES")
@IdClass(AlbumPriceId.class)
public class AlbumPrice extends AbstractPrice<AlbumPrice, Album> {
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

	/** The parent {@link Album} ID. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "album_id")
	@NotNull
	@Id
	@JsonBackReference
	private Album album;

	@Override
	public Date getDate() {
		return date;
	}

	@Override
	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public BigDecimal getPrice() {
		return price;
	}

	@Override
	public void setPrice(BigDecimal price) {
		this.price = price;
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

	@Override
	protected Album getModel() {
		return getAlbum();
	}

	@Override
	protected void setModel(Album model) {
		setAlbum(model);
	}
}
