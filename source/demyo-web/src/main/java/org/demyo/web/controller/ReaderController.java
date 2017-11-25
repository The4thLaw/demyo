package org.demyo.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import org.demyo.model.Album;
import org.demyo.model.Reader;
import org.demyo.model.Series;
import org.demyo.service.IModelService;
import org.demyo.service.IReaderService;

/**
 * Controller for {@link Reader} management.
 */
@Controller
@RequestMapping("/readers")
public class ReaderController extends AbstractModelController<Reader> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReaderController.class);

	@Autowired
	private IReaderService service;

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
		model.addAttribute("readerList", service.findAll());
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
	@RequestMapping(value = "/select/{readerId}", method = RequestMethod.GET)
	public String select(@PathVariable long readerId, Model model) {
		return redirect(model, "/");
	}

	/**
	 * JSON method to add a favourite {@link Series} for the current reader.
	 * 
	 * @param series The Series to add.
	 * @return Always <code>true</code>.
	 */
	@RequestMapping(value = "/favourites/series", method = RequestMethod.POST, //
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public boolean addFavouriteSeries(@RequestBody Series series) {
		service.addFavouriteSeries(series.getId());
		return true;
	}

	/**
	 * JSON method to add a favourite {@link Album} for the current reader.
	 * 
	 * @param albumId The Album to add.
	 * @return Always <code>true</code>.
	 */
	@RequestMapping(value = "/favourites/albums/{albumId}", method = RequestMethod.POST, //
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public boolean addFavouriteAlbum(@PathVariable long albumId) {
		LOGGER.debug("Adding favourite album {}", albumId);
		service.addFavouriteAlbum(albumId);
		return true;
	}

	/**
	 * JSON method to remove a favourite {@link Album} for the current reader.
	 * 
	 * @param albumId The Album to remove.
	 * @return Always <code>true</code>.
	 */
	@RequestMapping(value = "/favourites/albums/{albumId}", method = RequestMethod.DELETE, //
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public boolean removeFavouriteAlbum(@PathVariable long albumId) {
		LOGGER.debug("Removing favourite album {}", albumId);
		// service.addFavouriteAlbum(album.getId());
		return true;
	}

	@Override
	protected IModelService<Reader> getService() {
		return service;
	}

}
