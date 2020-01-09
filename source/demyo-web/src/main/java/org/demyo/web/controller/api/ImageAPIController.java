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

@RestController
@RequestMapping("/api/images")
public class ImageAPIController extends AbstractModelAPIController<Image> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ImageAPIController.class);
	private final IImageService service;

	@Autowired
	public ImageAPIController(IImageService service) {
		super(Image.class, service);
		this.service = service;
	}

	@GetMapping("/batch/{modelIds}")
	public List<Image> viewBatch(@PathVariable long[] modelIds) {
		List<Image> images = new ArrayList<>(modelIds.length);
		for (long modelId : modelIds) {
			images.add(service.getByIdForView(modelId));
		}
		return images;
	}

	/**
	 * @see IImageService#getImageDependencies(long)
	 */
	@GetMapping("/{modelId}/dependencies")
	@JsonView(ModelView.ImageDependencies.class)
	public Image dependencies(@PathVariable long modelId) {
		return service.getImageDependencies(modelId);
	}

	@GetMapping("/detect")
	public List<String> detectDiskImages() {
		return service.findUnknownDiskImages();
	}

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
