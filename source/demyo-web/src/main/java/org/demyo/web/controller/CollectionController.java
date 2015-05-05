package org.demyo.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.demyo.model.Collection;
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

/**
 * Controller for {@link Collection} management.
 * 
 * @author $Author: xr $
 * @version $Revision: 1084 $
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
	@RequestMapping(value = { "/delete/{id}" }, method = RequestMethod.POST)
	public String delete(@PathVariable long id, HttpServletRequest request, HttpServletResponse response) {
		getService().delete(id);
		return redirect("/publishers/"); // There's no index for Collections
	}
}
