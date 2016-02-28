package org.demyo.service;

import java.io.File;
import java.util.List;

import org.demyo.common.exception.DemyoException;
import org.demyo.model.Image;

/**
 * Service for management of {@link Image}s.
 */
public interface IImageService extends IModelService<Image> {
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
	 * @return The image file.
	 * @throws DemyoException In case of security or I/O error.
	 */
	File getImageThumbnail(long id) throws DemyoException;

	/**
	 * Processes the upload of an image.
	 * 
	 * @param originalFileName The name of the uploaded image.
	 * @param imageFile The file stored on disk, temporarily.
	 * @return The created or recovered image identifier.
	 * @throws DemyoException In case of error during file upload.
	 */
	long uploadImage(String originalFileName, File imageFile) throws DemyoException;

	/**
	 * Finds the list of all images that are currently on the disk, but not registered in Demyo.
	 * 
	 * @return The list of images.
	 */
	List<String> findUnknownDiskImages();

	/**
	 * Adds an image that is assumed to exist on the file system.
	 * 
	 * @param path The image to add.
	 * @return The created image identifier.
	 * @throws DemyoException In case of error during addition of the specified image.
	 */
	long addExistingImage(String path) throws DemyoException;

	/**
	 * Clears the thumbnail cache.
	 */
	void clearCachedThumbnails();

}
