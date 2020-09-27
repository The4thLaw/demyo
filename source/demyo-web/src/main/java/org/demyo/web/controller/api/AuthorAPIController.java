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
import org.demyo.model.filters.DerivativeFilter;
import org.demyo.service.IAuthorService;
import org.demyo.service.IDerivativeService;

/**
 * Controller handling the API calls for {@link Author}s.
 */
@RestController
@RequestMapping("/api/authors")
public class AuthorAPIController extends AbstractModelAPIController<Author> {
	private final IAuthorService service;
	private final IDerivativeService derivativeService;

	/**
	 * Creates the controller.
	 * 
	 * @param service The service to manage the entries.
	 * @param derivativeService The service to manage Derivatives.
	 */
	@Autowired
	public AuthorAPIController(IAuthorService service, IDerivativeService derivativeService) {
		super(service);
		this.service = service;
		this.derivativeService = derivativeService;
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

	/**
	 * Counts how many Derivatives use the given artist.
	 * 
	 * @param typeId The internal ID of the Author
	 * @return the count
	 */
	@GetMapping("{modelId}/derivatives/count")
	public long countDerivativesByArtist(@PathVariable long modelId) {
		return derivativeService.countDerivativesByFilter(DerivativeFilter.forArtist(modelId));
	}
}
