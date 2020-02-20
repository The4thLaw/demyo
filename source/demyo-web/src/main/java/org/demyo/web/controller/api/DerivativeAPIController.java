package org.demyo.web.controller.api;

import java.util.Arrays;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.demyo.common.exception.DemyoException;
import org.demyo.model.Derivative;
import org.demyo.model.filters.DerivativeFilter;
import org.demyo.service.IDerivativeService;

/**
 * Controller handling the API calls for {@link Derivative}s.
 */
@RestController
@RequestMapping("/api/derivatives")
public class DerivativeAPIController extends AbstractModelAPIController<Derivative> {
	private static final Logger LOGGER = LoggerFactory.getLogger(DerivativeAPIController.class);

	private final IDerivativeService service;

	/**
	 * Creates the controller.
	 * 
	 * @param service The service to manage the entries.
	 */
	@Autowired
	public DerivativeAPIController(IDerivativeService service) {
		super(service);
		this.service = service;
	}

	/**
	 * Retrieves the full list of the entities.
	 * 
	 * @param view The Jackson view to apply.
	 * @return The list.
	 */
	@Override
	@GetMapping({ "/", "/index" })
	public MappingJacksonValue index(@RequestParam("view") Optional<String> view) {
		Iterable<Derivative> value = service.findAllForIndex();
		return getIndexView(view, value);
	}

	@PostMapping({ "/index/filtered" })
	public MappingJacksonValue index(@RequestParam("view") Optional<String> view,
			@RequestBody DerivativeFilter filter) {
		Iterable<Derivative> value = service.findAllForIndex(filter);
		return getIndexView(view, value);
	}

	/**
	 * Saves / Commits the images uploaded through FilePond to the current Derivative.
	 * 
	 * @param modelId The Derivative ID.
	 * @param data The data from FilePond
	 * @return The view name.
	 * @throws DemyoException In case of error during recovery of the FilePond images.
	 */
	@PostMapping("/{modelId}/images")
	public boolean saveFromFilePond(@PathVariable long modelId,
			@RequestBody FilePondData data) throws DemyoException {
		String[] otherImages = data.getOtherImages();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Saving from FilePond: other images = {}",
					otherImages != null ? Arrays.asList(otherImages) : null);
		}

		service.recoverFromFilePond(modelId, otherImages);

		return true;
	}
}
