package org.demyo.web.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.demyo.model.DerivativeType;
import org.demyo.service.IDerivativeTypeService;

/**
 * Controller handling the API calls for {@link DerivativeType}s.
 */
@RestController
@RequestMapping("/api/derivativeTypes")
public class DerivativeTypeAPIController extends AbstractModelAPIController<DerivativeType> {
	/**
	 * Creates the controller.
	 * 
	 * @param service The service to manage the entries.
	 */
	@Autowired
	public DerivativeTypeAPIController(IDerivativeTypeService service) {
		super(service);
	}
}
