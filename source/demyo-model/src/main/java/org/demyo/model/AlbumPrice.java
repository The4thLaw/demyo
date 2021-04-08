package org.demyo.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * Defines a dated price for an {@link Album}.
 */
@Entity
@Table(name = "ALBUMS_PRICES")
@IdClass(AlbumPriceId.class)
public class AlbumPrice extends AbstractPrice<AlbumPrice, Album> {
	/** The parent {@link Album} ID. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "album_id")
	@NotNull
	@Id
	@JsonBackReference
	private Album album;

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
