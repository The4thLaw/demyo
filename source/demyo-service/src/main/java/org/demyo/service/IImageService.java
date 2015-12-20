package org.demyo.service;

import java.io.File;

import org.demyo.model.Image;
import org.demyo.model.exception.DemyoException;

/**
 * Service for management of {@link Image}s.
 */
public interface IImageService extends IModelServiceNG<Image> {

	/**
	 * Gets the actual file for an image from the collection.
	 * 
	 * @param path The path to the image.
	 * @return The image file.
	 * @throws DemyoException In case of security or I/O error.
	 */
	File getImageFile(String path) throws DemyoException;

	/**
	 * Gets the thumbnail file for an image from the collection. Thumbnails are generated if necessary and cached.
	 * 
	 * @param id The id of the image.
	 * @param path The path to the image.
	 * @return The image file.
	 * @throws DemyoException In case of security or I/O error.
	 */
	File getImageThumbnail(long id, String path) throws DemyoException;

}
