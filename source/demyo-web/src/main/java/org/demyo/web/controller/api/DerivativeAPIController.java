package org.demyo.web.controller.api;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.demyo.model.Derivative;
import org.demyo.service.IDerivativeService;

/**
 * Controller handling the API calls for {@link Derivative}s.
 */
@RestController
@RequestMapping("/api/derivatives")
public class DerivativeAPIController extends AbstractModelAPIController<Derivative> {
	private final IDerivativeService service;

	@Autowired
	public DerivativeAPIController(IDerivativeService service) {
		super(Derivative.class, service);
		this.service = service;
	}

	/**
	 * Retrieves the full list of the entities.
	 * 
	 * @param view The Jackson view to apply.
	 * @return The list.
	 */
	@Override
	@GetMapping({ "/", "/index" })
	public MappingJacksonValue index(@RequestParam("view") Optional<String> view) {
		List<Derivative> value = service.findAllForIndex();
		return getIndexView(view, value);
	}
}
