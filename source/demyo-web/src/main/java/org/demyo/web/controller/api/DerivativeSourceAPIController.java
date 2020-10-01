package org.demyo.web.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.demyo.model.DerivativeSource;
import org.demyo.model.filters.DerivativeFilter;
import org.demyo.service.IDerivativeService;
import org.demyo.service.IDerivativeSourceService;

/**
 * Controller handling the API calls for {@link DerivativeSource}s.
 */
@RestController
@RequestMapping("/api/derivativeSources")
public class DerivativeSourceAPIController extends AbstractModelAPIController<DerivativeSource> {
	private final IDerivativeService derivativeService;

	/**
	 * Creates the controller.
	 * 
	 * @param service The service to manage the entries.
	 * @param derivativeService The service to manage Derivatives.
	 */
	@Autowired
	public DerivativeSourceAPIController(IDerivativeSourceService service, IDerivativeService derivativeService) {
		super(service);
		this.derivativeService = derivativeService;
	}

	/**
	 * Counts how many Derivatives use the given type.
	 * 
	 * @param modelId The internal ID of the DerivativeType
	 * @return the count
	 */
	@GetMapping("{modelId}/derivatives/count")
	public long countDerivativesByType(@PathVariable long modelId) {
		return derivativeService.countDerivativesByFilter(DerivativeFilter.forSource(modelId));
	}
}
