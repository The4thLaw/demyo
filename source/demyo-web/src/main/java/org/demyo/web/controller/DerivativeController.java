package org.demyo.web.controller;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.demyo.model.Derivative;
import org.demyo.model.Image;
import org.demyo.model.QDerivative;
import org.demyo.service.IAuthorService;
import org.demyo.service.IDerivativeService;
import org.demyo.service.IDerivativeSourceService;
import org.demyo.service.IDerivativeTypeService;
import org.demyo.service.IImageService;
import org.demyo.service.IModelService;
import org.demyo.service.ISeriesService;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.querydsl.core.types.Predicate;

/**
 * Controller for {@link Derivative} management.
 */
@Controller
@RequestMapping("/derivatives")
public class DerivativeController extends AbstractModelController<Derivative> {
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
		registerCollectionEditor(binder, Set.class, "images", Image.class);
	}
}
