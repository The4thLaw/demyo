package org.demyo.web.controller;

import org.demyo.model.Publisher;
import org.demyo.service.IImageService;
import org.demyo.service.IModelServiceNG;
import org.demyo.service.IPublisherService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for {@link Publisher} management.
 */
@Controller
@RequestMapping("/publishers")
public class PublisherController extends AbstractModelControllerNG<Publisher> {
	@Autowired
	private IPublisherService service;
	@Autowired
	private IImageService imageService;

	/**
	 * Default constructor.
	 */
	public PublisherController() {
		super(Publisher.class, "publishers", "publisher");
	}

	@Override
	protected IModelServiceNG<Publisher> getService() {
		return service;
	}

	@Override
	protected void fillModelForEdition(Publisher entity, Model model) {
		model.addAttribute("images", imageService.findAll());
	}

	@Override
	protected void postProcessValidationError(Publisher entity, BindingResult result) {
		translateError(result, "logoId", "logo.id");
	}
}
