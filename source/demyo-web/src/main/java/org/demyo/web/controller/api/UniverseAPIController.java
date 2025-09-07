package org.demyo.web.controller.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import org.demyo.common.exception.DemyoException;
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

	/**
	 * Saves / Commits the image uploaded through FilePond to the current Universe.
	 *
	 * @param modelId The Universe ID.
	 * @param data The data from FilePond
	 * @return The view name.
	 * @throws DemyoException In case of error during recovery of the FilePond images.
	 */
	@PostMapping("/{modelId}/images")
	public boolean saveFromFilePond(@PathVariable("modelId") long modelId,
			@RequestBody FilePondData data) throws DemyoException {
		return saveFromFilePond(data, () -> service.recoverFromFilePond(modelId, data.getMainImage(), data.getOtherImages()));
	}
}
