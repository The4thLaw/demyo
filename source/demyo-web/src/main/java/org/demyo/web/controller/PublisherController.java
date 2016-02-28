package org.demyo.web.controller;

import java.util.List;

import org.demyo.model.Collection;
import org.demyo.model.ModelView;
import org.demyo.model.Publisher;
import org.demyo.service.ICollectionService;
import org.demyo.service.IImageService;
import org.demyo.service.IModelService;
import org.demyo.service.IPublisherService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * Controller for {@link Publisher} management.
 */
@Controller
@RequestMapping("/publishers")
public class PublisherController extends AbstractModelController<Publisher> {
	@Autowired
	private IPublisherService service;
	@Autowired
	private IImageService imageService;
	@Autowired
	private ICollectionService collectionService;

	/**
	 * Default constructor.
	 */
	public PublisherController() {
		super(Publisher.class, "publishers", "publisher");
	}

	@JsonView(ModelView.Minimal.class)
	@RequestMapping(value = "/{id}/collections", method = RequestMethod.GET, consumes = "application/json",
			produces = "application/json")
	public @ResponseBody
	List<Collection> getCollectionsForPublisher(@PathVariable("id") long publisherId) {
		return collectionService.findByPublisherId(publisherId);
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
