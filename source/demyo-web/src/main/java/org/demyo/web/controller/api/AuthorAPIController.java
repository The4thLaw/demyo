package org.demyo.web.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.demyo.model.Author;
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

	@Override
	protected IAuthorService getService() {
		return service;
	}

}
