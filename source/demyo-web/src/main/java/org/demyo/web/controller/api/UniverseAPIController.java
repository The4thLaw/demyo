package org.demyo.web.controller.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import org.demyo.model.Album;
import org.demyo.model.ModelView;
import org.demyo.model.Universe;
import org.demyo.service.IUniverseService;

/**
 * Controller handling the API calls for {@link Universe}s.
 */
@RestController
@RequestMapping("/api/universes")
public class UniverseAPIController extends AbstractModelAPIController<Universe> {
	private final IUniverseService service;

	/**
	 * Creates the controller.
	 *
	 * @param service The service to manage the entries.
	 */
	@Autowired
	public UniverseAPIController(IUniverseService service) {
		super(service);
		this.service = service;
	}

	/**
	 * Finds the Albums which are part of a Universe, either directly or through its Series.
	 * @param id The Universe ID
	 * @return The found Albums
	 */
	@GetMapping("/{modelId}/albums")
	@JsonView(ModelView.Basic.class)
	public List<Album> getContents(@PathVariable("modelId") long id) {
		return service.getContents(id);
	}
}
