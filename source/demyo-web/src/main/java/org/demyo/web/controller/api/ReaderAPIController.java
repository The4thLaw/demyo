package org.demyo.web.controller.api;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.demyo.model.Album;
import org.demyo.model.Reader;
import org.demyo.model.Series;
import org.demyo.model.beans.MetaSeries;
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
	 * Checks if it's safe to delete readers.
	 * <p>
	 * The library cannot be left without at least one reader.
	 * </p>
	 *
	 * @return <code>true</code> if it's safe to delete one reader.
	 */
	@GetMapping("mayDelete")
	public boolean mayDeleteReader() {
		return service.mayDeleteReader();
	}

	/**
	 * Gets the {@link ReaderLists} for a specific {@link Reader}.
	 *
	 * @param modelId The {@link Reader} ID.
	 * @return The lists
	 */
	@GetMapping("/{modelId}/lists")
	public ReaderLists getLists(@PathVariable("modelId") long modelId) {
		return service.getLists(modelId);
	}

	/**
	 * Gets the Reader's favourite Albums.
	 *
	 * @param modelId The {@link Reader} ID.
	 * @param view The Jackson view to apply.
	 * @return The favourite Albums, as a list of {@link MetaSeries}
	 */
	@GetMapping("/{modelId}/favourites/albums")
	public MappingJacksonValue getFavouriteAlbums(@PathVariable("modelId") long modelId,
			@RequestParam("view") Optional<String> view) {
		Iterable<MetaSeries> value = service.getFavouriteAlbums(modelId);
		return getIndexView(view, value);
	}

	/**
	 * Gets the Reader's reading list.
	 *
	 * @param modelId The {@link Reader} ID.
	 * @param view The Jackson view to apply.
	 * @return The Albums, as a list of {@link MetaSeries}
	 */
	@GetMapping("/{modelId}/readingList/albums")
	public MappingJacksonValue getReadingList(@PathVariable("modelId") long modelId,
			@RequestParam("view") Optional<String> view) {
		Iterable<MetaSeries> value = service.getReadingListAlbums(modelId);
		return getIndexView(view, value);
	}

	/**
	 * Add a favourite {@link Series} for the specified reader.
	 *
	 * @param modelId The {@link Reader} ID.
	 * @param seriesId The Series to add.
	 * @return Always <code>true</code>.
	 */
	@PostMapping("/{modelId}/favourites/series/{seriesId}")
	public boolean addFavouriteSeries(@PathVariable("modelId") long modelId, @PathVariable("seriesId") long seriesId) {
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
	public boolean removeFavouriteSeries(@PathVariable("modelId") long modelId, @PathVariable("seriesId") long seriesId) {
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
	public boolean addFavouriteAlbum(@PathVariable("modelId") long modelId, @PathVariable("albumId") long albumId) {
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
	public boolean removeFavouriteAlbum(@PathVariable("modelId") long modelId, @PathVariable("albumId") long albumId) {
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
	public boolean addAlbumToReadingList(@PathVariable("modelId") long modelId, @PathVariable("albumId") long albumId) {
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
	public boolean removeAlbumFromReadingList(@PathVariable("modelId") long modelId,
			@PathVariable("albumId") long albumId) {
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
	public Set<Number> addSeriesToReadingList(@PathVariable("modelId") long modelId,
			@PathVariable("seriesId") long seriesId) {
		service.addSeriesToReadingList(modelId, seriesId);
		return service.getReadingList(modelId);
	}

}
