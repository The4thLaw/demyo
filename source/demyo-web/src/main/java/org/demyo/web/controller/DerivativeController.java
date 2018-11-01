package org.demyo.web.controller;

import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.querydsl.core.types.Predicate;

import org.demyo.common.exception.DemyoException;
import org.demyo.model.Derivative;
import org.demyo.model.Image;
import org.demyo.model.QDerivative;
import org.demyo.model.util.IdentifyingNameComparator;
import org.demyo.service.IAuthorService;
import org.demyo.service.IDerivativeService;
import org.demyo.service.IDerivativeSourceService;
import org.demyo.service.IDerivativeTypeService;
import org.demyo.service.IImageService;
import org.demyo.service.IModelService;
import org.demyo.service.ISeriesService;

/**
 * Controller for {@link Derivative} management.
 */
@Controller
@RequestMapping("/derivatives")
public class DerivativeController extends AbstractModelController<Derivative> {
	private static final Logger LOGGER = LoggerFactory.getLogger(DerivativeController.class);
	private static final String REQUEST_PARAM_DERIV_TYPE = "withType";

	@Autowired
	private IDerivativeService service;
	@Autowired
	private ISeriesService seriesService;
	@Autowired
	private IAuthorService authorService;
	@Autowired
	private IDerivativeTypeService derivativeTypeService;
	@Autowired
	private IDerivativeSourceService derivativeSourceService;
	@Autowired
	private IImageService imageService;

	/**
	 * Default constructor.
	 */
	public DerivativeController() {
		super(Derivative.class, "derivatives", "derivative");
	}

	@Override
	@RequestMapping(value = { "/", "/index" }, method = RequestMethod.GET)
	public String index(@RequestParam(value = "page", required = false) Integer currentPage,
			@RequestParam(value = "startsWith", required = false) Character startsWith, Model model,
			HttpServletRequest request) {
		currentPage = getCurrentPage(currentPage, startsWith);

		Long derivativeTypeId = getLongParam(request, REQUEST_PARAM_DERIV_TYPE);
		Predicate filter = null;
		if (derivativeTypeId != null) {
			filter = QDerivative.derivative.type.id.eq(derivativeTypeId);
		}

		Slice<Derivative> entities = service.findPaginated(currentPage, filter);

		model.addAttribute("derivativeList", entities);

		return "derivatives/index";
	}

	/**
	 * Views an index of all Derivatives in a page intended to be printed for stickers.
	 * 
	 * @param model The view model.
	 * @return The view name.
	 */
	@GetMapping("/stickers")
	public String stickerSheet(Model model) {
		// List<Derivative> derivatives = service.findAll();
		List<Derivative> derivatives = service.findAllForStickers();
		model.addAttribute("derivativeList", derivatives);
		setLayoutPlain(model);
		return "derivatives/stickers";
	}

	/**
	 * Saves / Commits the images uploaded through FilePond to the current Album.
	 * 
	 * @param modelId The Derivative ID.
	 * @param otherImageIds The ID for alternate images.
	 * @param model The view model.
	 * @return The view name.
	 * @throws DemyoException In case of error during recovery of the FilePond images.
	 */
	@PostMapping("/{modelId}/filepond")
	public String saveFromFilePond(@PathVariable long modelId,
			@RequestParam(value = MODEL_KEY_FILEPOND_OTHER, required = false) String[] otherImageIds, Model model)
			throws DemyoException {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Saving from FilePond: other images = {}",
					otherImageIds != null ? Arrays.asList(otherImageIds) : null);
		}

		service.recoverFromFilePond(modelId, otherImageIds);

		return redirect(model, "/derivatives/view/" + modelId);
	}

	@Override
	protected IModelService<Derivative> getService() {
		return service;
	}

	@Override
	protected void fillModelForEdition(Derivative entity, Model model) {
		model.addAttribute("series", seriesService.findAll());
		model.addAttribute("authors", authorService.findAll());
		model.addAttribute("types", derivativeTypeService.findAll());
		model.addAttribute("sources", derivativeSourceService.findAll());
		model.addAttribute("images", imageService.findAll());
	}

	@InitBinder
	private void initBinder(PropertyEditorRegistry binder) throws Exception {
		registerCollectionEditor(binder, SortedSet.class, "images", Image.class, new IdentifyingNameComparator());
	}
}
