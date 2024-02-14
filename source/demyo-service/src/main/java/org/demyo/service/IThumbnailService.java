package org.demyo.service;

import org.the4thlaw.commons.services.image.BaseThumbnailService.ImageSupplier;
import org.the4thlaw.commons.services.image.ImageRetrievalResponse;

import org.demyo.common.exception.DemyoException;

/**
 * Defines methods to work with thumbnails.
 */
public interface IThumbnailService {
	/**
	 * Sets the maximum pool size for the thumbnail executor.
	 * <p>
	 * This method must be ran periodically based on the JavaDoc of {@link Runtime#availableProcessors()} and
	 * {@link Runtime#maxMemory()}.
	 * </p>
	 */
	void setThumbnailPoolSize();

	/**
	 * Gets the thumbnail for an image. Results are cached.
	 *
	 * @param id The image ID
	 * @param maxWidth The maximum desired width
	 * @param lenient When lenient, we allow a minor difference in sizes, because it won't change a lot in terms of
	 *            bandwidth and will save disk space and processing power. The returned image could thus be larger than
	 *            the maximum width
	 * @param imageFileLoader a way to lazily load the image file if needed.
	 * @return The resized image
	 * @throws DemyoException In case of security or I/O error.
	 */
	ImageRetrievalResponse getThumbnail(long id, int maxWidth, boolean lenient, ImageSupplier imageFileLoader)
			throws DemyoException;

}
