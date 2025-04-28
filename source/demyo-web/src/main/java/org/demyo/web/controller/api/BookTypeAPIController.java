package org.demyo.web.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.demyo.model.BookType;
import org.demyo.model.filters.AlbumFilter;
import org.demyo.service.IAlbumService;
import org.demyo.service.impl.IBookTypeService;


/**
 * Controller handling the API calls for {@link BookType}s.
 */
@RestController
@RequestMapping("/api/bookTypes")
public class BookTypeAPIController extends AbstractModelAPIController<BookType> {
	private final IBookTypeService service;
	private final IAlbumService albumService;

	/**
	 * Creates the controller.
	 *
	 * @param service The service to manage the entries.
	 * @param albumService The service to manage albums.
	 */
	@Autowired
	public BookTypeAPIController(IBookTypeService service, IAlbumService albumService) {
		super(service);
		this.service = service;
		this.albumService = albumService;
	}

	/**
	 * Checks if book type management has been explicitly enabled.
	 *
	 * @return <code>true</code> if it's enabled.
	 */
	@GetMapping("management/enabled")
	public boolean isManagementEnabled() {
		return service.isManagementEnabled();
	}

	/**
	 * Enables book type management.
	 */
	@PostMapping("management/enable")
	public void enableManagement() {
		service.enableManagement();
	}

	/**
	 * Counts how many Albums use the given {@link BookType}
	 *
	 * @param modelId The internal ID of the {@link BookType}
	 * @return the count
	 */
	@GetMapping("{modelId}/albums/count")
	public long countAlbumsByBinding(@PathVariable("modelId") long modelId) {
		return albumService.countAlbumsByFilter(AlbumFilter.forBookType(modelId));
	}
}
