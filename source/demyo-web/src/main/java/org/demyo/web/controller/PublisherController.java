package org.demyo.web.controller;

import org.demyo.model.Publisher;
import org.demyo.service.IImageService;
import org.demyo.service.IModelService;
import org.demyo.service.IPublisherService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for {@link Publisher} management.
 * 
 * @author $Author: xr $
 * @version $Revision: 1082 $
 */
@Controller
@RequestMapping("/publishers")
public class PublisherController extends AbstractModelController<Publisher> {
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
	protected IModelService<Publisher> getService() {
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
