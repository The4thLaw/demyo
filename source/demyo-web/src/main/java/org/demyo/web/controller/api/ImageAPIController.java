package org.demyo.web.controller.api;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import org.demyo.common.exception.DemyoException;
import org.demyo.model.Image;
import org.demyo.model.ModelView;
import org.demyo.service.IImageService;

/**
 * Controller handling the API calls for {@link Image}s.
 */
@RestController
@RequestMapping("/api/images")
public class ImageAPIController extends AbstractModelAPIController<Image> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ImageAPIController.class);
	private final IImageService service;

	/**
	 * Creates the controller.
	 * 
	 * @param service The service to manage the entries.
	 */
	@Autowired
	public ImageAPIController(IImageService service) {
		super(service);
		this.service = service;
	}

	/**
	 * Retrieves a batch of images, usually after confirmation of a batch upload.
	 * 
	 * @param modelIds The identifiers of all images to view.
	 * @return The images.
	 */
	@GetMapping("/batch/{modelIds}")
	public List<Image> viewBatch(@PathVariable long[] modelIds) {
		List<Image> images = new ArrayList<>(modelIds.length);
		for (long modelId : modelIds) {
			images.add(service.getByIdForView(modelId));
		}
		return images;
	}

	/**
	 * Returns an Image and its dependent albums, authors, etc.
	 * 
	 * @param modelId The image ID
	 * @return The Image object, populated with its dependencies.
	 * @see IImageService#getImageDependencies(long)
	 */
	@GetMapping("/{modelId}/dependencies")
	@JsonView(ModelView.ImageDependencies.class)
	public Image dependencies(@PathVariable long modelId) {
		return service.getImageDependencies(modelId);
	}

	/**
	 * Gets the list of images found on the disk but not yet present in the database.
	 * 
	 * @return The paths to the images.
	 */
	@GetMapping("/detect")
	public List<String> detectDiskImages() {
		return service.findUnknownDiskImages();
	}

	/**
	 * Saves a set of images found through {@link #detectDiskImages()}.
	 * 
	 * @param paths The paths to the images to add.
	 * @return The added image IDs. Can be used in {@link #viewBatch(long[])}.
	 * @throws DemyoException In case of error while adding the images.
	 */
	@PostMapping("/detect")
	public List<Long> saveDetectedImages(@RequestBody List<String> paths) throws DemyoException {
		LOGGER.debug("Selected images: {}", paths);

		List<Long> addedImages = new ArrayList<>(paths.size());
		for (String path : paths) {
			addedImages.add(service.addExistingImage(path));
		}

		return addedImages;
	}
}
