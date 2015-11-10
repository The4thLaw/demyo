package org.demyo.web.controller;

import org.demyo.model.Author;
import org.demyo.service.IAuthorService;
import org.demyo.service.IImageService;
import org.demyo.service.IModelServiceNG;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for {@link Author} management.
 */
@Controller
@RequestMapping("/authors")
public class AuthorController extends AbstractModelControllerNG<Author> {
	@Autowired
	private IAuthorService service;
	@Autowired
	private IImageService imageService;

	/**
	 * Default constructor.
	 */
	public AuthorController() {
		super(Author.class, "authors", "author");
	}

	// TODO: get albums he's worked on on "view" action

	@Override
	protected IModelServiceNG<Author> getService() {
		return service;
	}

	@Override
	protected void fillModelForEdition(Author entity, Model model) {
		model.addAttribute("images", imageService.findAll());
	}

	@Override
	protected void postProcessValidationError(Author entity, BindingResult result) {
		translateError(result, "portraitId", "portrait.id");
	}
}
