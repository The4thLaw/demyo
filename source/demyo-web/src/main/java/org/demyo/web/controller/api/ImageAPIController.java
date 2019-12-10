package org.demyo.web.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.demyo.model.Image;
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

	@Override
	protected IImageService getService() {
		return service;
	}

}
