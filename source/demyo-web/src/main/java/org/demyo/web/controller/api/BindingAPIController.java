package org.demyo.web.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.demyo.model.Binding;
import org.demyo.service.IAlbumService;
import org.demyo.service.IBindingService;

/**
 * Controller handling the API calls for {@link Binding}s.
 */
@RestController
@RequestMapping("/api/bindings")
public class BindingAPIController extends AbstractModelAPIController<Binding> {
	private final IAlbumService albumService;

	/**
	 * Creates the controller.
	 * 
	 * @param service The service to manage the entries.
	 * @param albumService The service to manage the albums.
	 */
	@Autowired
	public BindingAPIController(IBindingService service, IAlbumService albumService) {
		super(service);
		this.albumService = albumService;
	}

	/**
	 * Counts how many Albums use the given Binding.
	 * 
	 * @param modelId The internal ID of the Binding
	 * @return the count
	 */
	@GetMapping("{modelId}/albums/count")
	int countAlbumsByBinding(@PathVariable long modelId) {
		return albumService.countAlbumsByBinding(modelId);
	}
}
