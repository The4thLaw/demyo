package org.demyo.web.controller.api;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.demyo.model.Album;
import org.demyo.model.Reader;
import org.demyo.model.Series;
import org.demyo.model.beans.ReaderLists;
import org.demyo.service.IReaderService;

/**
 * Controller handling the API calls for {@link Reader}s.
 */
@RestController
@RequestMapping("/api/readers")
public class ReaderAPIController extends AbstractModelAPIController<Reader> {
	private final IReaderService service;

	/**
	 * Creates the controller.
	 * 
	 * @param service The service to manage the entries.
	 */
	@Autowired
	public ReaderAPIController(IReaderService service) {
		super(service);
		this.service = service;
	}

	/**
	 * Automatically selects a Reader if possible.
	 * 
	 * @return The Reader, or <code>null</code> if one cannot be automatically selected.
	 */
	@GetMapping("autoSelect")
	public Reader autoSelect() {
		return service.getUniqueReader();
	}

	/**
	 * Gets the {@link ReaderLists} for a specific {@link Reader}.
	 * 
	 * @param modelId The {@link Reader} ID.
	 * @return The lists
	 */
	@GetMapping("/{modelId}/lists")
	public ReaderLists getLists(@PathVariable long modelId) {
		return service.getLists(modelId);
	}

	/**
	 * Add a favourite {@link Series} for the specified reader.
	 * 
	 * @param modelId The {@link Reader} ID.
	 * @param seriesId The Series to add.
	 * @return Always <code>true</code>.
	 */
	@PostMapping("/{modelId}/favourites/series/{seriesId}")
	public boolean addFavouriteSeries(@PathVariable long modelId, @PathVariable long seriesId) {
		service.addFavouriteSeries(modelId, seriesId);
		return true;
	}

	/**
	 * Removes a favourite {@link Series} from the specified reader.
	 * 
	 * @param modelId The {@link Reader} ID.
	 * @param seriesId The Series to remove.
	 * @return Always <code>true</code>.
	 */
	@DeleteMapping("/{modelId}/favourites/series/{seriesId}")
	public boolean removeFavouriteSeries(@PathVariable long modelId, @PathVariable long seriesId) {
		service.removeFavouriteSeries(modelId, seriesId);
		return true;
	}

	/**
	 * Adds a favourite {@link Album} for the specified reader.
	 * 
	 * @param modelId The {@link Reader} ID.
	 * @param albumId The Album to add.
	 * @return Always <code>true</code>.
	 */
	@PostMapping("/{modelId}/favourites/albums/{albumId}")
	public boolean addFavouriteAlbum(@PathVariable long modelId, @PathVariable long albumId) {
		service.addFavouriteAlbum(modelId, albumId);
		return true;
	}

	/**
	 * Removes a favourite {@link Album} from the specified reader.
	 * 
	 * @param modelId The {@link Reader} ID.
	 * @param albumId The Album to remove.
	 * @return Always <code>true</code>.
	 */
	@DeleteMapping("/{modelId}/favourites/albums/{albumId}")
	public boolean removeFavouriteAlbum(@PathVariable long modelId, @PathVariable long albumId) {
		service.removeFavouriteAlbum(modelId, albumId);
		return true;
	}

	/**
	 * Adds an {@link Album} to the reading list of the specified reader.
	 * 
	 * @param modelId The {@link Reader} ID.
	 * @param albumId The Album to add.
	 * @return Always <code>true</code>.
	 */
	@PostMapping("/{modelId}/readingList/albums/{albumId}")
	public boolean addAlbumToReadingList(@PathVariable long modelId, @PathVariable long albumId) {
		service.addAlbumToReadingList(modelId, albumId);
		return true;
	}

	/**
	 * Removes an {@link Album} from the reading list of the specified reader.
	 * 
	 * @param modelId The {@link Reader} ID.
	 * @param albumId The Album to add.
	 * @return Always <code>true</code>.
	 */
	@DeleteMapping("/{modelId}/readingList/albums/{albumId}")
	public boolean removeAlbumFromReadingList(@PathVariable long modelId, @PathVariable long albumId) {
		service.removeAlbumFromReadingList(modelId, albumId);
		return true;
	}

	/**
	 * Adds an entire {@link Series} to the reading list of the specified reader.
	 * 
	 * @param modelId The {@link Reader} ID.
	 * @param seriesId The Series to add.
	 * @return The updated reading list.
	 */
	@PostMapping("/{modelId}/readingList/series/{seriesId}")
	public Set<Number> addSeriesToReadingList(@PathVariable long modelId, @PathVariable long seriesId) {
		service.addSeriesToReadingList(modelId, seriesId);
		return service.getReadingList(modelId);
	}

}
