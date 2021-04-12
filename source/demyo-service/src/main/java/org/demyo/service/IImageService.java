package org.demyo.service;

import java.io.File;
import java.util.List;
import java.util.Optional;

import org.springframework.core.io.Resource;

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

	/**
	 * Retrieves images from FilePond and stores them in the database.
	 * 
	 * @param baseImageName The base image description to use. Depending on <code>alwaysNumber</code>, may be suffixed
	 *            by a number.
	 * @param alwaysNumber Whether to always suffix the base image name or not.
	 * @param filePondIds The IDs of the FilePond files to recover.
	 * @return The recovered images.
	 * @throws DemyoException In case of issue while retrieving the files.
	 */
	List<Image> recoverImagesFromFilePond(String baseImageName, boolean alwaysNumber, String... filePondIds)
			throws DemyoException;

	/**
	 * Gets an image, resized if necessary.
	 * 
	 * @param id The image ID
	 * @param maxWidthOpt The maximum desired width, if any
	 * @param lenient When lenient, we allow a 10% difference in sizes, because it won't change a lot in terms of
	 *            bandwidth and will save disk space and processing power. The returned image could thus be larger than
	 *            the maximum width
	 * @return The resized image
	 * @throws DemyoException In case of security or I/O error.
	 */
	Resource getImage(long id, Optional<Integer> maxWidthOpt, boolean lenient) throws DemyoException;

	/**
	 * Returns an Image and its dependent albums, authors, etc.
	 * 
	 * @param id The image ID
	 * @return The Image object, populated with its dependencies.
	 */
	Image getImageDependencies(long id);

	/**
	 * Sets the maximum pool size for the thumbnail executor.
	 * <p>
	 * This method must be ran periodically based on the JavaDoc of {@link Runtime#availableProcessors()} and
	 * {@link Runtime#maxMemory()}.
	 * </p>
	 */
	void setThumbnailPoolSize();
}
