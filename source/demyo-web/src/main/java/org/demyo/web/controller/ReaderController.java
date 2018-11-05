package org.demyo.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.querydsl.core.types.Predicate;

import org.demyo.model.Album;
import org.demyo.model.QAlbum;
import org.demyo.model.Reader;
import org.demyo.model.Series;
import org.demyo.service.IAlbumService;
import org.demyo.service.IModelService;
import org.demyo.service.IReaderContext;
import org.demyo.service.IReaderService;

/**
 * Controller for {@link Reader} management.
 */
@Controller
@RequestMapping("/readers")
public class ReaderController extends AbstractModelController<Reader> {
	@Autowired
	private IReaderService service;
	@Autowired
	private IAlbumService albumService;
	@Autowired
	private IReaderContext readerContext;

	/**
	 * Default constructor.
	 */
	public ReaderController() {
		super(Reader.class, "readers", "reader");
	}

	/**
	 * Displays the reader selection page.
	 * 
	 * @param currentPage Unused.
	 * @param model The view model.
	 * @param startsWith Unused.
	 * @param request Unused.
	 * @return The view name.
	 */
	@Override
	@RequestMapping(value = { "/", "/index" }, method = RequestMethod.GET)
	public String index(@RequestParam(value = "page", required = false) Integer currentPage,
			@RequestParam(value = "startsWith", required = false) Character startsWith, Model model,
			HttpServletRequest request) {
		List<Reader> readers = service.findAll();
		model.addAttribute("readerList", readers);
		model.addAttribute("canDelete", readers.size() > 1);
		setLayoutMinimal(model);
		setShadedBody(model);
		return "readers/index";
	}

	/**
	 * Saves changes to a new or updated entity.
	 * 
	 * @param entity The entity to save.
	 * @param result The result of the binding and validation.
	 * @param model The view model.
	 * @param request The HTTP request.
	 * @param response The HTTP response.
	 * @return The view name.
	 */
	@Override
	@RequestMapping(value = { "/add", "/edit/{modelId}" }, method = RequestMethod.POST)
	public String save(@Valid Reader entity, BindingResult result, Model model, HttpServletRequest request,
			HttpServletResponse response) {
		// Override to be able to redirect elsewhere
		super.save(entity, result, model, request, response);

		if (result.hasErrors()) {
			return "readers/add-edit";
		}

		// If the edited reader is the logged in one, clear the context so that his name is updated automatically
		// Other readers will have to re-select the reader, clear their session or restart Demyo so that the session
		// beans are reloaded
		// Maybe use a toast message to notify this?
		Reader currentReader = readerContext.getCurrentReader();
		if (currentReader != null && currentReader.getId().equals(entity.getId())) {
			readerContext.clearCurrentReader();
		}

		return redirect(model, "/readers/index");
	}

	/**
	 * Selects the desired reader.
	 * <p>
	 * Actually does nothing, as the actual selection is done in the interceptor.
	 * <p>
	 * 
	 * @param readerId The ID of the {@link Reader} to select.
	 * @param model The view model.
	 * @return The view name.
	 */
	// We need this method in order not to get a 404
	@RequestMapping(value = "/{readerId}/select", method = RequestMethod.GET)
	public String select(@PathVariable long readerId, Model model) {
		return redirect(model, "/");
	}

	/**
	 * JSON method to add a favourite {@link Series} for the current reader.
	 * 
	 * @param seriesId The Series to add.
	 * @return Always <code>true</code>.
	 */
	@RequestMapping(value = "/current/favourites/series/{seriesId}", method = RequestMethod.POST, //
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public boolean addFavouriteSeries(@PathVariable long seriesId) {
		service.addFavouriteSeries(seriesId);
		return true;
	}

	/**
	 * JSON method to remove a favourite {@link Series} for the current reader.
	 * 
	 * @param seriesId The Series to remove.
	 * @return Always <code>true</code>.
	 */
	@RequestMapping(value = "/current/favourites/series/{seriesId}", method = RequestMethod.DELETE, //
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public boolean removeFavouriteSeries(@PathVariable long seriesId) {
		service.removeFavouriteSeries(seriesId);
		return true;
	}

	/**
	 * JSON method to add a favourite {@link Album} for the current reader.
	 * 
	 * @param albumId The Album to add.
	 * @return Always <code>true</code>.
	 */
	@RequestMapping(value = "/current/favourites/albums/{albumId}", method = RequestMethod.POST, //
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public boolean addFavouriteAlbum(@PathVariable long albumId) {
		service.addFavouriteAlbum(albumId);
		return true;
	}

	/**
	 * JSON method to remove a favourite {@link Album} for the current reader.
	 * 
	 * @param albumId The Album to remove.
	 * @return Always <code>true</code>.
	 */
	@RequestMapping(value = "/current/favourites/albums/{albumId}", method = RequestMethod.DELETE, //
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public boolean removeFavouriteAlbum(@PathVariable long albumId) {
		service.removeFavouriteAlbum(albumId);
		return true;
	}

	/**
	 * View the favourites for a specific reader.
	 * 
	 * @param currentPage The current page number (starting from 1). Can be missing.
	 * @param model The view model.
	 * @param readerId The {@link Reader} to view favourites from.
	 * @return The view name.
	 */
	@RequestMapping(value = "/{readerId}/favourites", method = RequestMethod.GET)
	public String favourites(@RequestParam(value = "page", required = false) Integer currentPage,
			@PathVariable long readerId, Model model) {
		currentPage = getCurrentPage(currentPage, null);

		Predicate filter = null;

		filter = QAlbum.album.readersFavourites.any().id.eq(readerId)
				.or(QAlbum.album.series.readersFavourites.any().id.eq(readerId));

		Slice<Album> entities = albumService.findPaginated(currentPage, filter);

		model.addAttribute("albumList", entities);
		model.addAttribute("preventAdd", true);

		return "albums/index";
	}

	/**
	 * View the reading list for a specific reader.
	 * 
	 * @param currentPage The current page number (starting from 1). Can be missing.
	 * @param model The view model.
	 * @param readerId The {@link Reader} to view the list of.
	 * @return The view name.
	 */
	@RequestMapping(value = "/{readerId}/readingList", method = RequestMethod.GET)
	public String readingList(@RequestParam(value = "page", required = false) Integer currentPage,
			@PathVariable long readerId, Model model) {
		currentPage = getCurrentPage(currentPage, null);

		Predicate filter = null;

		filter = QAlbum.album.readersReadingList.any().id.eq(readerId);

		Slice<Album> entities = albumService.findPaginated(currentPage, filter);

		model.addAttribute("albumList", entities);
		model.addAttribute("preventAdd", true);

		return "albums/index";
	}

	/**
	 * JSON method to add an {@link Album} to the reading list of the current reader.
	 * 
	 * @param albumId The Album to add.
	 * @return Always <code>true</code>.
	 */
	@RequestMapping(value = "/current/readingList/albums/{albumId}", method = RequestMethod.POST, //
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public boolean addAlbumToReadingList(@PathVariable long albumId) {
		service.addAlbumToReadingList(albumId);
		return true;
	}

	/**
	 * JSON method to add an {@link Album} to the reading list of the current reader.
	 * 
	 * @param albumId The Album to add.
	 * @return Always <code>true</code>.
	 */
	@RequestMapping(value = "/current/readingList/albums/{albumId}", method = RequestMethod.DELETE, //
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public boolean removeAlbumFromReadingList(@PathVariable long albumId) {
		service.removeAlbumFromReadingList(albumId);
		return true;
	}

	/**
	 * JSON method to add an entire {@link Series} to the reading list of the current reader.
	 * 
	 * @param seriesId The Series to add.
	 * @param model The view model.
	 * @return The view name.
	 */
	@RequestMapping(value = "/current/readingList/series/{seriesId}", method = RequestMethod.POST)
	public String addSeriesToReadingList(@PathVariable long seriesId, Model model) {
		service.addSeriesToReadingList(seriesId);
		return redirect(model, "/series/view/" + seriesId);
	}

	@Override
	protected IModelService<Reader> getService() {
		return service;
	}

}
