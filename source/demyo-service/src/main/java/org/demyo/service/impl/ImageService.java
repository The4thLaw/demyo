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

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

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
import org.the4thlaw.commons.services.image.ImageRetrievalResponse;
import org.the4thlaw.commons.utils.io.FilenameUtils;

import org.demyo.common.config.SystemConfiguration;
import org.demyo.common.exception.DemyoErrorCode;
import org.demyo.common.exception.DemyoException;
import org.demyo.common.exception.DemyoRuntimeException;
import org.demyo.dao.IImageRepo;
import org.demyo.dao.IModelRepo;
import org.demyo.model.Image;
import org.demyo.service.IFilePondService;
import org.demyo.service.IImageService;
import org.demyo.service.IThumbnailService;

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

	private final Path uploadDirectory;

	/**
	 * Default constructor.
	 *
	 * @throws DemyoException In case of issue while creating the upload directory.
	 */
	public ImageService() throws DemyoException {
		super(Image.class);

		uploadDirectory = SystemConfiguration.getInstance().getImagesDirectory().resolve(UPLOAD_DIRECTORY_NAME);
		try {
			Files.createDirectories(uploadDirectory);
		} catch (IOException e) {
			throw new DemyoException(DemyoErrorCode.IMAGE_IO_ERROR, e);
		}
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
	public Path getImageFile(@NotNull Image imageModel) {
		String path = imageModel.getUrl();
		Path image = SystemConfiguration.getInstance().getImagesDirectory().resolve(path);
		validateImagePath(image);
		if (!Files.isRegularFile(image)) {
			throw new DemyoRuntimeException(DemyoErrorCode.IMAGE_NOT_FOUND, path);
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

	private static void validateImagePath(Path image)  {
		try {
			Path imagesDirectoryPath = SystemConfiguration.getInstance().getImagesDirectory().toRealPath();
			Path imagePath = image.toRealPath();
			if (!imagePath.startsWith(imagesDirectoryPath)) {
				throw new DemyoRuntimeException(DemyoErrorCode.IMAGE_DIRECTORY_TRAVERSAL, imagePath.toString());
			}
		} catch (IOException e) {
			throw new DemyoRuntimeException(DemyoErrorCode.SYS_IO_ERROR, e);
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
		String extension = FilenameUtils.getFileExtension(originalFileName);
		if (extension == null) {
			extension = "jpg";
		}
		String targetFileName = hash + "." + extension;
		Path targetFile = uploadDirectory.resolve(targetFileName);

		// Check if the file exists already
		String targetFileUrl = UPLOAD_DIRECTORY_NAME + "/" + targetFileName;
		if (Files.exists(targetFile)) {
			LOGGER.debug("Uploaded file {} already exists as {}", originalFileName, targetFile);
			// Reuse the same image
			Image foundImage = repo.findByUrl(targetFileUrl);

			if (foundImage != null) {
				LOGGER.debug("Already existing image found with ID {}", foundImage.getId());
				return foundImage;
			}
			LOGGER.debug("Already existing image was not found in the database. Uploading it as a new one...");
			org.the4thlaw.commons.utils.io.FileUtils.deleteQuietly(targetFile);
		}

		// Either the file does not exist, or it was removed in the previous step
		// Move the image to its final destination
		try {
			Files.move(imageFile.toPath(), targetFile);
		} catch (IOException e) {
			LOGGER.error("Failed to rename {} to {}", imageFile, targetFile, e);
			throw new DemyoException(DemyoErrorCode.IMAGE_IO_ERROR);
		}

		// Create a new image with the right attributes
		Image image = new Image();
		image.setUrl(targetFileUrl);

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
		Path imagesDirectory = SystemConfiguration.getInstance().getImagesDirectory();
		Path targetFile = imagesDirectory.resolve(path);
		validateImagePath(targetFile);

		// Create a new image with the right attributes
		Image image = new Image();
		image.setUrl(path);

		image.setDescription(inferDescription(targetFile.getFileName().toString()));

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
		File imagesDirectory = SystemConfiguration.getInstance().getImagesDirectory().toFile();

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
		Path thumbnailDirectory = SystemConfiguration.getInstance().getThumbnailDirectory();
		org.the4thlaw.commons.utils.io.FileUtils.deleteDirectoryQuietly(thumbnailDirectory);
		try {
			Files.createDirectories(thumbnailDirectory);
			LOGGER.debug("Recreated thumbnail directory at {}", thumbnailDirectory);
		} catch (IOException e) {
			LOGGER.warn("Failed to create directory {}", thumbnailDirectory, e);
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
				Files.delete(getImageFile(image));
			} catch (IOException | RuntimeException e) {
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
