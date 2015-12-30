package org.demyo.service.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.transaction.Transactional;

import org.demyo.dao.IImageRepo;
import org.demyo.dao.IModelRepo;
import org.demyo.model.Image;
import org.demyo.model.config.ApplicationConfiguration;
import org.demyo.model.config.SystemConfiguration;
import org.demyo.model.exception.DemyoErrorCode;
import org.demyo.model.exception.DemyoException;
import org.demyo.model.exception.DemyoRuntimeException;
import org.demyo.service.IConfigurationService;
import org.demyo.service.IImageService;
import org.demyo.utils.io.DIOUtils;

import org.apache.commons.codec.digest.DigestUtils;
import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.imgscalr.Scalr.Mode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implements the contract defined by {@link IImageService}.
 */
// TODO: delete the file as well on image deletion
@Service
public class ImageService extends AbstractModelService<Image> implements IImageService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ImageService.class);
	private static final String UPLOAD_DIRECTORY_NAME = "uploads";

	@Autowired
	private IImageRepo repo;
	@Autowired
	private IConfigurationService configService;
	private File uploadDirectory;

	/**
	 * Default constructor.
	 */
	public ImageService() {
		super(Image.class);

		uploadDirectory = new File(SystemConfiguration.getInstance().getImagesDirectory(), UPLOAD_DIRECTORY_NAME);
		uploadDirectory.mkdirs();
	}

	@Override
	protected IModelRepo<Image> getRepo() {
		return repo;
	}

	@Override
	@Deprecated
	public File getImageFile(String path) throws DemyoException {
		File image = new File(SystemConfiguration.getInstance().getImagesDirectory(), path);
		validateImagePath(image);
		if (!image.isFile()) {
			throw new DemyoException(DemyoErrorCode.IMAGE_NOT_FOUND, path);
		}
		return image;
	}

	@Override
	public File getImageFile(Image imageModel) throws DemyoException {
		String path = imageModel.getUrl();
		File image = new File(SystemConfiguration.getInstance().getImagesDirectory(), path);
		validateImagePath(image);
		if (!image.isFile()) {
			throw new DemyoException(DemyoErrorCode.IMAGE_NOT_FOUND, path);
		}
		return image;
	}

	@Override
	public File getImageThumbnail(long id, String path) throws DemyoException {
		// Check cache
		File thumb = new File(SystemConfiguration.getInstance().getThumbnailDirectory(), id + ".jpg");
		if (thumb.exists()) {
			return thumb;
		}

		// No cache hit, generate thumbnail
		File image = getImageFile(path);
		long time = System.currentTimeMillis();
		BufferedImage buffImage;
		try {
			buffImage = ImageIO.read(image);
		} catch (IOException e) {
			throw new DemyoRuntimeException(DemyoErrorCode.SYS_IO_ERROR, e);
		}
		// If the image has one dimension too small, constrain it to that
		ApplicationConfiguration config = configService.getConfiguration();
		int desiredMaxWidth = config.getThumbnailWidth() > buffImage.getWidth() ? buffImage.getWidth() : config
				.getThumbnailWidth();
		int desiredMaxHeight = config.getThumbnailHeight() > buffImage.getHeight() ? buffImage.getHeight()
				: config.getThumbnailHeight();
		// Resize
		BufferedImage buffThumb = Scalr.resize(buffImage, Method.BALANCED, Mode.AUTOMATIC, desiredMaxWidth,
				desiredMaxHeight, Scalr.OP_ANTIALIAS);
		buffImage.flush();
		LOGGER.debug("Thumbnail generated in {}ms", System.currentTimeMillis() - time);

		try {
			ImageIO.write(buffThumb, "jpg", thumb);
		} catch (IOException e) {
			throw new DemyoRuntimeException(DemyoErrorCode.SYS_IO_ERROR, e);
		}
		buffThumb.flush();

		return thumb;
	}

	private void validateImagePath(File image) throws DemyoException {
		try {
			String imagesDirectoryPath = SystemConfiguration.getInstance().getImagesDirectory().getCanonicalPath();
			String imagePath = image.getCanonicalPath();
			if (!imagePath.startsWith(imagesDirectoryPath)) {
				throw new DemyoException(DemyoErrorCode.IMAGE_DIRECTORY_TRAVERSAL, imagePath);
			}
		} catch (IOException e) {
			throw new DemyoRuntimeException(DemyoErrorCode.SYS_IO_ERROR, e);
		}
	}

	@Override
	@Transactional
	public long uploadImage(String originalFileName, File imageFile) {
		// Determine the hash of the uploaded file
		String hash;
		try (FileInputStream data = new FileInputStream(imageFile)) {
			hash = DigestUtils.sha256Hex(data);
		} catch (IOException e) {
			throw new DemyoRuntimeException(DemyoErrorCode.IMAGE_UPLOAD_ERROR, e);
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

		// Infer a description (strip any path attributes)
		String description = new File(originalFileName).getName();
		// Strip the extension
		description = description.replaceAll("\\.[^.]+$", "");
		// Convert underscores into spaces
		description = description.replace('_', ' ');
		image.setDescription(description);
		LOGGER.debug("Uploaded file {} led to description: {}", originalFileName, description);

		return save(image);
	}
}
