package org.demyo.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import org.demyo.model.Collection;
import org.demyo.service.ICollectionService;
import org.demyo.service.IImageService;
import org.demyo.service.IModelService;
import org.demyo.service.IPublisherService;

/**
 * Controller for {@link Collection} management.
 */
@Controller
@RequestMapping("/collections")
public class CollectionController extends AbstractModelController<Collection> {
	@Autowired
	private ICollectionService service;
	@Autowired
	private IPublisherService publisherService;
	@Autowired
	private IImageService imageService;

	/**
	 * Default constructor.
	 */
	public CollectionController() {
		super(Collection.class, "collections", "collection");
	}

	/**
	 * Adds a {@link Collection}.
	 * 
	 * @param model The view model.
	 * @param publisherId The ID of the {@link org.demyo.model.Publisher Publisher} to add the collection to.
	 * @return The view name.
	 */
	// params attributes allows overriding the parent /add
	@RequestMapping(value = "/add", params = { "toPublisher" }, method = RequestMethod.GET)
	public String add(Model model, @RequestParam(name = "toPublisher", required = true) long publisherId) {
		Collection collection = new Collection();
		collection.setPublisher(publisherService.getByIdForEdition(publisherId));
		model.addAttribute("collection", collection);
		fillModelForEdition(null, model);

		return "collections/add-edit";
	}

	@Override
	protected IModelService<Collection> getService() {
		return service;
	}

	@Override
	protected void fillModelForEdition(Collection entity, Model model) {
		model.addAttribute("publishers", publisherService.findAll());
		model.addAttribute("images", imageService.findAll());
	}

	@Override
	protected void postProcessValidationError(Collection entity, BindingResult result) {
		translateError(result, "logoId", "logo.id");
		translateError(result, "publisherId", "publisher.id");
	}

	@Override
	@RequestMapping(value = { "/delete/{modelId}" }, method = RequestMethod.POST)
	public String delete(@PathVariable long modelId, Model model, HttpServletRequest request,
			HttpServletResponse response) {
		getService().delete(modelId);
		return redirect(model, "/publishers/"); // There's no index for Collections
	}
}
