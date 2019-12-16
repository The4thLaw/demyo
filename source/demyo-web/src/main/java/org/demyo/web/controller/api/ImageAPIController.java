package org.demyo.web.controller.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import org.demyo.model.Image;
import org.demyo.model.ModelView;
import org.demyo.service.IImageService;

@RestController
@RequestMapping("/api/images")
public class ImageAPIController extends AbstractModelAPIController<Image> {
	private final IImageService service;

	@Autowired
	public ImageAPIController(IImageService service) {
		super(Image.class);
		this.service = service;
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

	@Override
	protected IImageService getService() {
		return service;
	}

}
