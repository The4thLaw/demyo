package org.demyo.web.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.demyo.model.Author;
import org.demyo.model.beans.AuthorAlbums;
import org.demyo.service.IAuthorService;

@RestController
@RequestMapping("/api/authors")
public class AuthorAPIController extends AbstractModelAPIController<Author> {
	private final IAuthorService service;

	@Autowired
	public AuthorAPIController(IAuthorService service) {
		super(Author.class);
		this.service = service;
	}

	/**
	 * @see IAuthorService#getAuthorAlbums(long)
	 */
	@GetMapping("/{modelId}/albums")
	public AuthorAlbums getAuthorAlbums(@PathVariable("modelId") long id) {
		return service.getAuthorAlbums(id);
	}

	@Override
	protected IAuthorService getService() {
		return service;
	}

}
