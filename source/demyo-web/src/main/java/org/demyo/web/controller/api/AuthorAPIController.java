package org.demyo.web.controller.api;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import org.demyo.model.Author;
import org.demyo.model.ModelView;
import org.demyo.model.Taxon;
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

	@Override
    @GetMapping({ "/", "/index" })
	public MappingJacksonValue index(@RequestParam("view") Optional<String> view) {
		List<Author> value = service.findAll(true);
		return getIndexView(view, value);
	}

	/**
	 * Retrieves the list of real authors.
	 *
	 * @param view The Jackson view to apply.
	 * @return The list.
	 */
	@GetMapping({ "/real" })
	public MappingJacksonValue indexReal(@RequestParam("view") Optional<String> view) {
		List<Author> value = service.findAll(false);
		return getIndexView(view, value);
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
	 * Gets the genres in which a specific author is active.
	 *
	 * @param id The Author internal identifier
	 * @return The genres.
	 */
	@GetMapping("/{modelId}/genres")
	@JsonView(ModelView.Basic.class)
	public List<Taxon> getAuthorGenres(@PathVariable("modelId") long id) {
		return service.getAuthorGenres(id);
	}

	/**
	 * Counts how many Derivatives use the given artist.
	 *
	 * @param modelId The internal ID of the Author
	 * @return the count
	 */
	@GetMapping("{modelId}/derivatives/count")
	public long countDerivativesByArtist(@PathVariable("modelId") long modelId) {
		return derivativeService.countDerivativesByFilter(DerivativeFilter.forArtist(modelId));
	}
}
