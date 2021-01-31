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
import org.demyo.model.Album;
import org.demyo.model.beans.MetaSeries;
import org.demyo.model.filters.AlbumFilter;
import org.demyo.model.filters.DerivativeFilter;
import org.demyo.service.IAlbumService;
import org.demyo.service.IDerivativeService;

/**
 * Controller handling the API calls for {@link Albums}s.
 */
@RestController
@RequestMapping("/api/albums")
public class AlbumAPIController extends AbstractModelAPIController<Album> {
	private static final Logger LOGGER = LoggerFactory.getLogger(AlbumAPIController.class);

	private final IAlbumService service;
	private final IDerivativeService derivativeService;

	/**
	 * Creates the controller.
	 * 
	 * @param service The service to manage the Albums.
	 * @param derivativeService The service to manage the Derivatives.
	 */
	@Autowired
	public AlbumAPIController(IAlbumService service, IDerivativeService derivativeService) {
		super(service);
		this.service = service;
		this.derivativeService = derivativeService;
	}

	/**
	 * Retrieves the full list of Albums.
	 * 
	 * @param view The Jackson view to apply.
	 * @return The list.
	 */
	@Override
	@GetMapping(
	{ "/", "/index" })
	public MappingJacksonValue index(@RequestParam("view") Optional<String> view) {
		Iterable<MetaSeries> value = service.findAllForIndex();
		return getIndexView(view, value);
	}

	/**
	 * Retrieves the filtered list of Albums.
	 * 
	 * @param view The Jackson view to apply.
	 * @param filter The filter to apply.
	 * @return The list.
	 */
	@PostMapping({ "/index/filtered" })
	public MappingJacksonValue index(@RequestParam("view") Optional<String> view,
			@RequestBody AlbumFilter filter) {
		Iterable<MetaSeries> value = service.findAllForIndex(filter);
		return getIndexView(view, value);
	}

	/**
	 * Saves / Commits the images uploaded through FilePond to the current Album.
	 * 
	 * @param modelId The Album ID.
	 * @param data The data from FilePond
	 * @return The view name.
	 * @throws DemyoException In case of error during recovery of the FilePond images.
	 */
	@PostMapping("/{modelId}/images")
	public boolean saveFromFilePond(@PathVariable long modelId,
			@RequestBody FilePondData data) throws DemyoException {
		String mainImage = data.getMainImage();
		String[] otherImages = data.getOtherImages();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Saving from FilePond: main image = {}, other images = {}",
					mainImage, otherImages != null ? Arrays.asList(otherImages) : null);
		}

		service.recoverFromFilePond(modelId, mainImage, otherImages);

		return true;
	}

	/**
	 * Counts how many Derivatives use the given Album.
	 * 
	 * @param modelId The internal ID of the {@link Album}
	 * @return the count
	 */
	@GetMapping("{modelId}/derivatives/count")
	public long countDerivativesByAlbums(@PathVariable long modelId) {
		return derivativeService.countDerivativesByFilter(DerivativeFilter.forAlbum(modelId));
	}
}
