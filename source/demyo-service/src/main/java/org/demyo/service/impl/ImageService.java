package org.demyo.service.impl;

import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.hibernate.validator.constraints.NotEmpty;
import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.imgscalr.Scalr.Mode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.demyo.common.config.SystemConfiguration;
import org.demyo.common.exception.DemyoErrorCode;
import org.demyo.common.exception.DemyoException;
import org.demyo.dao.IImageRepo;
import org.demyo.dao.IModelRepo;
import org.demyo.model.Image;
import org.demyo.model.config.ApplicationConfiguration;
import org.demyo.service.IConfigurationService;
import org.demyo.service.IImageService;
import org.demyo.utils.io.DIOUtils;

/**
 * Implements the contract defined by {@link IImageService}.
 */
@Service
public class ImageService extends AbstractModelService<Image> implements IImageService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ImageService.class);
	private static final String UPLOAD_DIRECTORY_NAME = "uploads";

	@Autowired
	private IImageRepo repo;
	@Autowired
	private IConfigurationService configService;
	private final File uploadDirectory;

	/**
	 * Default constructor.
	 */
	public ImageService() {
		super(Image.class);

		uploadDirectory = new File(SystemConfiguration.getInstance().getImagesDirectory(), UPLOAD_DIRECTORY_NAME);
		uploadDirectory.mkdirs();
	}

	@Override
	@Transactional(readOnly = true)
	public Image getByIdForView(long id) {
		Image entity = repo.findOneForView(id);
		if (entity == null) {
			throw new EntityNotFoundException("No Image for ID " + id);
		}
		return entity;
	}

	@Override
	protected IModelRepo<Image> getRepo() {
		return repo;
	}

	@Override
	public File getImageFile(@NotNull Image imageModel) throws DemyoException {
		String path = imageModel.getUrl();
		File image = new File(SystemConfiguration.getInstance().getImagesDirectory(), path);
		validateImagePath(image);
		if (!image.isFile()) {
			throw new DemyoException(DemyoErrorCode.IMAGE_NOT_FOUND, path);
		}
		return image;
	}

	@Override
	@Transactional(readOnly = true)
	public File getImageThumbnail(long id) throws DemyoException {
		// Check cache (two possible formats)
		File pngThumb = new File(SystemConfiguration.getInstance().getThumbnailDirectory(), id + ".png");
		File jpgThumb = new File(SystemConfiguration.getInstance().getThumbnailDirectory(), id + ".jpg");
		if (pngThumb.exists()) {
			return pngThumb;
		}
		if (jpgThumb.exists()) {
			return jpgThumb;
		}

		// No cache hit, generate thumbnail
		File image = getImageFile(getByIdForEdition(id));
		long time = System.currentTimeMillis();
		BufferedImage buffImage;
		try {
			buffImage = ImageIO.read(image);
		} catch (IOException e) {
			throw new DemyoException(DemyoErrorCode.SYS_IO_ERROR, e);
		}

		// If the image has one dimension too small, constrain it to that
		ApplicationConfiguration config = configService.getConfiguration();
		int desiredMaxWidth = config.getThumbnailWidth() > buffImage.getWidth() ? buffImage.getWidth()
				: config.getThumbnailWidth();
		int desiredMaxHeight = config.getThumbnailHeight() > buffImage.getHeight() ? buffImage.getHeight()
				: config.getThumbnailHeight();

		// Resize
		BufferedImage buffThumb = Scalr.resize(buffImage, Method.AUTOMATIC, Mode.AUTOMATIC, desiredMaxWidth,
				desiredMaxHeight, Scalr.OP_ANTIALIAS);
		LOGGER.debug("Thumbnail generated in {}ms", System.currentTimeMillis() - time);

		try {
			// Write opaque images as JPG, transparent images as PNG
			if (Transparency.OPAQUE == buffThumb.getTransparency()) {
				ImageIO.write(buffThumb, "jpg", jpgThumb);
				return jpgThumb;
			} else {
				ImageIO.write(buffThumb, "png", pngThumb);
				return pngThumb;
			}
		} catch (IOException e) {
			// Ensure we don't store invalid contents
			jpgThumb.delete();
			pngThumb.delete();
			throw new DemyoException(DemyoErrorCode.IMAGE_IO_ERROR, e);
		} finally {
			buffImage.flush();
			buffThumb.flush();
		}
	}

	private void validateImagePath(File image) throws DemyoException {
		try {
			String imagesDirectoryPath = SystemConfiguration.getInstance().getImagesDirectory().getCanonicalPath();
			String imagePath = image.getCanonicalPath();
			if (!imagePath.startsWith(imagesDirectoryPath)) {
				throw new DemyoException(DemyoErrorCode.IMAGE_DIRECTORY_TRAVERSAL, imagePath);
			}
		} catch (IOException e) {
			throw new DemyoException(DemyoErrorCode.SYS_IO_ERROR, e);
		}
	}

	@Transactional(rollbackFor = Throwable.class)
	@CacheEvict(cacheNames = "ModelLists", key = "#root.targetClass.simpleName.replaceAll('Service$', '')")
	@Override
	public long uploadImage(@NotEmpty String originalFileName, @NotNull File imageFile) throws DemyoException {
		// Determine the hash of the uploaded file
		String hash;
		try (FileInputStream data = new FileInputStream(imageFile)) {
			hash = DigestUtils.sha256Hex(data);
		} catch (IOException e) {
			throw new DemyoException(DemyoErrorCode.IMAGE_UPLOAD_ERROR, e);
		}

		// Determine the target file name
		String extension = DIOUtils.getFileExtension(originalFileName);
		if (extension == null) {
			extension = "jpg";
		}
		File targetFile = new File(uploadDirectory, hash + "." + extension);

		// Check if the file exists already
		if (targetFile.exists()) {
			LOGGER.debug("Uploaded file {} already exists as {}", originalFileName, targetFile);
			// Reuse the same image
			Image foundImage = repo.findByUrl(UPLOAD_DIRECTORY_NAME + "/" + targetFile.getName());

			if (foundImage != null) {
				LOGGER.debug("Already existing image found with ID {}", foundImage.getId());
				return foundImage.getId();
			}
			LOGGER.debug("Already existing image was not found in the database. Uploading it as a new one...");
			targetFile.delete();
		}

		// Either the file does not exist, or it was removed in the previous step
		// Move the image to its final destination
		imageFile.renameTo(targetFile);

		// Create a new image with the right attributes
		Image image = new Image();
		image.setUrl(UPLOAD_DIRECTORY_NAME + "/" + targetFile.getName());

		image.setDescription(inferDescription(originalFileName));

		return save(image);
	}

	@Transactional(rollbackFor = Throwable.class)
	@CacheEvict(cacheNames = "ModelLists", key = "#root.targetClass.simpleName.replaceAll('Service$', '')")
	@Override
	public long addExistingImage(@NotEmpty String path) throws DemyoException {
		File imagesDirectory = SystemConfiguration.getInstance().getImagesDirectory();
		File targetFile = new File(imagesDirectory, path);
		if (!targetFile.toPath().startsWith(imagesDirectory.toPath())) {
			throw new DemyoException(DemyoErrorCode.IMAGE_DIRECTORY_TRAVERSAL, path + " is not a valid path");
		}

		// Create a new image with the right attributes
		Image image = new Image();
		image.setUrl(path);

		image.setDescription(inferDescription(targetFile.getName()));

		return save(image);
	}

	private static String inferDescription(String originalFileName) {
		// Strip any path attributes
		String description = new File(originalFileName).getName();
		// Strip the extension
		description = description.replaceAll("\\.[^.]+$", "");
		// Convert underscores into spaces
		description = description.replace('_', ' ');
		LOGGER.debug("Uploaded file {} led to description: {}", originalFileName, description);
		return description;
	}

	@Override
	@Transactional(readOnly = true)
	public List<String> findUnknownDiskImages() {
		List<String> unknownFiles = new ArrayList<>();
		File imagesDirectory = SystemConfiguration.getInstance().getImagesDirectory();

		// Find the files
		Collection<File> foundFiles = FileUtils.listFiles(imagesDirectory,
				new SuffixFileFilter(Arrays.asList(".jpg", ".jpeg", ".png", ".gif"), IOCase.INSENSITIVE),
				TrueFileFilter.INSTANCE);

		// Find the paths known in the database
		Set<String> knownFiles = repo.findAllPaths();

		// Filter out the known files
		Path imagesDirectoryPath = imagesDirectory.toPath();
		LOGGER.trace("Known files: {}", knownFiles);
		for (File file : foundFiles) {
			String relativePath = imagesDirectoryPath.relativize(file.toPath()).toString();
			// Normalize file separators so that collections imported and exported between OS's still work
			relativePath = relativePath.replace(File.separatorChar, '/');
			boolean known = knownFiles.contains(relativePath);
			LOGGER.trace("Is {} known? {}", relativePath, known);

			if (!known) {
				unknownFiles.add(relativePath);
			}
		}

		Collections.sort(unknownFiles);

		return unknownFiles;
	}

	@Override
	public void clearCachedThumbnails() {
		LOGGER.debug("Clearing thumbnail cache");
		File thumbnailDirectory = SystemConfiguration.getInstance().getThumbnailDirectory();
		DIOUtils.deleteDirectory(thumbnailDirectory);
		if (!thumbnailDirectory.mkdir()) {
			LOGGER.warn("Failed to create directory {}", thumbnailDirectory);
		} else {
			LOGGER.debug("Recreated thumbnail directory at {}", thumbnailDirectory);
		}
	}

	@Transactional(rollbackFor = Throwable.class)
	@CacheEvict(cacheNames = "ModelLists", key = "#root.targetClass.simpleName.replaceAll('Service$', '')")
	@Override
	public void delete(long id) {
		Image image = getRepo().findOne(id);
		super.delete(id);
		String path = image.getUrl();
		if (path.startsWith(UPLOAD_DIRECTORY_NAME)) {
			LOGGER.info("Image {} was uploaded automatically as {}; deleting it now that it is no longer used", id,
					path);
			try {
				if (!getImageFile(image).delete()) {
					LOGGER.warn("Failed to delete the image at {}", path);
				}
			} catch (DemyoException e) {
				LOGGER.warn("Failed to delete the image at {}", path, e);
			}
		}
	}
}
