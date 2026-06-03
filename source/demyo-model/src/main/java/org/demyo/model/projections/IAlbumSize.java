package org.demyo.model.projections;

import java.math.BigDecimal;

/**
 * Structures a search for common album sizes.
 */
public interface IAlbumSize {
	/**
	 * Gets the source of the common size.
	 */
	String getSource();

	/**
	 * Gets how many times this size occurred.
	 */
	long getCnt();

	/**
	 * Gets the found height.
	 */
	BigDecimal getHeight();

	/**
	 * Gets the found width.
	 */
	BigDecimal getWidth();
}
