package org.demyo.service;

import java.io.File;

import org.demyo.model.Image;
import org.demyo.common.exception.DemyoException;

/**
 * Service for management of {@link Image}s.
 */
public interface IImageService extends IModelService<Image> {

	/**
	 * Gets the actual file for an image from the collection.
	 * 
	 * @param path The path to the image.
	 * @return The image file.
	 * @throws DemyoException In case of security or I/O error.
	 */
	@Deprecated
	File getImageFile(String path) throws DemyoException;

	/**
	 * Gets the actual file for an image from the collection.
	 * 
	 * @param image The image to get the file.
	 * @return The image file on disk.
	 * @throws DemyoException In case of security or I/O error.
	 */
	File getImageFile(Image image) throws DemyoException;

	/**
	 * Gets the thumbnail file for an image from the collection. Thumbnails are generated if necessary and cached.
	 * 
	 * @param id The id of the image.
	 * @param path The path to the image.
	 * @return The image file.
	 * @throws DemyoException In case of security or I/O error.
	 */
	File getImageThumbnail(long id, String path) throws DemyoException;

	/**
	 * Processes the upload of an image.
	 * 
	 * @param originalFileName The name of the uploaded image.
	 * @param imageFile The file stored on disk, temporarily.
	 * @return The created or recovered image.
	 */
	long uploadImage(String originalFileName, File imageFile);

}
