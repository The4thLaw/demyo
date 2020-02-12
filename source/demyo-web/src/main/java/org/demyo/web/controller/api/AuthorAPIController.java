package org.demyo.web.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import org.demyo.model.Author;
import org.demyo.model.ModelView;
import org.demyo.model.beans.AuthorAlbums;
import org.demyo.service.IAuthorService;

/**
 * Controller handling the API calls for {@link Author}s.
 */
@RestController
@RequestMapping("/api/authors")
public class AuthorAPIController extends AbstractModelAPIController<Author> {
	private final IAuthorService service;

	/**
	 * Creates the controller.
	 * 
	 * @param service The service to manage the entries.
	 */
	@Autowired
	public AuthorAPIController(IAuthorService service) {
		super(service);
		this.service = service;
	}

	/**
	 * Gets the albums to which a specific author participated.
	 * 
	 * @param id The Author internal identifier
	 * @return The structured works
	 */
	@GetMapping("/{modelId}/albums")
	@JsonView(ModelView.Basic.class)
	public AuthorAlbums getAuthorAlbums(@PathVariable("modelId") long id) {
		return service.getAuthorAlbums(id);
	}
}
