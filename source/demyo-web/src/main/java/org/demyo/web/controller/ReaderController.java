package org.demyo.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import org.demyo.model.Reader;
import org.demyo.service.IModelService;
import org.demyo.service.IReaderService;

/**
 * Controller for {@link Reader} management.
 */
@Controller
@RequestMapping("/readers")
public class ReaderController extends AbstractModelController<Reader> {

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

	@Override
	protected IModelService<Reader> getService() {
		return service;
	}

}
