package org.demyo.web.controller.api;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.demyo.common.exception.DemyoException;
import org.demyo.model.Collection;
import org.demyo.model.filters.AlbumFilter;
import org.demyo.service.IAlbumService;
import org.demyo.service.ICollectionService;

/**
 * Controller handling the API calls for {@link Collection}s.
 */
@RestController
@RequestMapping("/api/collections")
public class CollectionAPIController extends AbstractModelAPIController<Collection> {
	private final ICollectionService service;
	private final IAlbumService albumService;

	/**
	 * Creates the controller.
	 *
	 * @param service The service to manage the entries.
	 * @param albumService The service to manage the albums.
	 */
	@Autowired
	public CollectionAPIController(ICollectionService service, IAlbumService albumService) {
		super(service);
		this.service = service;
		this.albumService = albumService;
	}

	/**
	 * Counts how many Albums use the given {@link Collection}.
	 *
	 * @param modelId The internal ID of the {@link Collection}
	 * @return the count
	 */
	@GetMapping("{modelId}/albums/count")
	public long countAlbumsByBinding(@PathVariable("modelId") long modelId) {
		return albumService.countAlbumsByFilter(AlbumFilter.forCollection(modelId));
	}

	/**
	 * Saves / Commits the image uploaded through FilePond to the current Collection.
	 *
	 * @param modelId The Collection ID.
	 * @param data The data from FilePond
	 * @return The view name.
	 * @throws DemyoException In case of error during recovery of the FilePond images.
	 */
	@PostMapping("/{modelId}/images")
	public boolean saveFromFilePond(@PathVariable("modelId") long modelId,
			@RequestBody FilePondData data) throws DemyoException {
		return saveFromFilePond(data, () -> service.recoverFromFilePond(modelId, data.getMainImage()));
	}
}
