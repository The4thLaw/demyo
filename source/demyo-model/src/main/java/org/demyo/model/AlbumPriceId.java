package org.demyo.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Specific class for the {@link AlbumPrice} ID.
 */
public class AlbumPriceId implements Serializable {
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
		if (other instanceof AlbumPriceId otherId) {
			return album.equals(otherId.album) && date.equals(otherId.date);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return album.hashCode() ^ date.hashCode();
	}

	@Override
	public String toString() {
		return "AlbumPriceId(album_id=" + album + ", date=" + date + ")";
	}
}
