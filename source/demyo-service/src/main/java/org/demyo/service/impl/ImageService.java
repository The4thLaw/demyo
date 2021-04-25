package org.demyo.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
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
import org.demyo.service.IFilePondService;
import org.demyo.service.IImageService;
import org.demyo.service.IThumbnailService;
import org.demyo.service.ImageRetrievalResponse;
import org.demyo.utils.io.DIOUtils;

/**
 * Implements the contract defined by {@link IImageService}.
 */
@Service
public class ImageService extends AbstractModelService<Image> implements IImageService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ImageService.class);
	private static final String UPLOAD_DIRECTORY_NAME = "uploads";
	private static final Pattern FILE_EXT_PATTERN = Pattern.compile("\\.[^.]+$");

	@Autowired
	private IImageRepo repo;
	@Autowired
	private IFilePondService filePondService;
	@Autowired
	private IThumbnailService thumbnailService;

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
	@Transactional(readOnly = true)
	public Image getImageDependencies(long id) {
		Image entity = repo.findOneForDependencies(id);
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
	public ImageRetrievalResponse getImage(long id, Optional<Integer> maxWidthOpt, boolean lenient)
			throws DemyoException {
		if (!maxWidthOpt.isPresent()) {
			Image image = getByIdForEdition(id);
			return new ImageRetrievalResponse(getImageFile(image));
		}

		return thumbnailService.getThumbnail(id, maxWidthOpt.get(), lenient, () -> getImageFile(getByIdForEdition(id)));
	}

	private static void validateImagePath(File image) throws DemyoException {
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
		return uploadImage(originalFileName, null, imageFile).getId();
	}

	private Image uploadImage(String originalFileName, String description, File imageFile) throws DemyoException {
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
				return foundImage;
			}
			LOGGER.debug("Already existing image was not found in the database. Uploading it as a new one...");
			DIOUtils.delete(targetFile);
		}

		// Either the file does not exist, or it was removed in the previous step
		// Move the image to its final destination
		if (!imageFile.renameTo(targetFile)) {
			LOGGER.error("Failed to rename {} to {}", imageFile, targetFile);
			throw new DemyoException(DemyoErrorCode.IMAGE_IO_ERROR);
		}

		// Create a new image with the right attributes
		Image image = new Image();
		image.setUrl(UPLOAD_DIRECTORY_NAME + "/" + targetFile.getName());

		if (description == null) {
			image.setDescription(inferDescription(originalFileName));
		} else {
			image.setDescription(description);
		}

		return saveAndGetModel(image);
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
		FILE_EXT_PATTERN.matcher(description).replaceAll("");
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
		Image image = getRepo().getOne(id);
		super.delete(id);
		String path = image.getUrl();
		if (path.startsWith(UPLOAD_DIRECTORY_NAME)) {
			LOGGER.info("Image {} was uploaded automatically as {}; deleting it now that it is no longer used", id,
					path);
			try {
				Files.delete(getImageFile(image).toPath());
			} catch (IOException | DemyoException e) {
				LOGGER.warn("Failed to delete the image at {}", path, e);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Throwable.class)
	@CacheEvict(cacheNames = "ModelLists", key = "#root.targetClass.simpleName.replaceAll('Service$', '')")
	public List<Image> recoverImagesFromFilePond(String baseImageName, boolean alwaysNumber, String... filePondIds)
			throws DemyoException {
		if (filePondIds == null || filePondIds.length == 0) {
			return Collections.emptyList();
		}

		List<Image> newImages = new ArrayList<>();

		if (filePondIds.length > 1 && !alwaysNumber) {
			throw new DemyoException(DemyoErrorCode.IMAGE_FILEPOND_RENUMBER_MORE_THAN_ONE);
		}

		List<Image> existingImages = repo.findByDescriptionLike(baseImageName + '%');
		Set<String> imageNames = new HashSet<>(existingImages.size());
		for (Image img : existingImages) {
			imageNames.add(img.getDescription());
		}

		int currentNumber = 1;
		for (String id : filePondIds) {
			String currentDescription;
			while (true) {
				if (!alwaysNumber) {
					currentDescription = baseImageName;
				} else {
					currentDescription = baseImageName + " " + currentNumber;
				}
				// If we were numbering already, increase the counter. If not, we want to start at 1
				if (alwaysNumber) {
					currentNumber++;
				}
				if (!imageNames.contains(currentDescription)) {
					break;
				}
				// Next time, we should consider numbering no matter what
				alwaysNumber = true;
				LOGGER.debug("{} already exists, searching further", currentDescription);
			}
			LOGGER.debug("Found a suitable image name: {}", currentDescription);
			File fpFile = filePondService.getFileForId(id);
			newImages.add(uploadImage(fpFile.getName(), currentDescription, fpFile));
		}

		return newImages;
	}
}
