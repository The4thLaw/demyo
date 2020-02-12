package org.demyo.web.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.demyo.model.DerivativeSource;
import org.demyo.service.IDerivativeSourceService;

/**
 * Controller handling the API calls for {@link DerivativeSource}s.
 */
@RestController
@RequestMapping("/api/derivativeSources")
public class DerivativeSourceAPIController extends AbstractModelAPIController<DerivativeSource> {
	/**
	 * Creates the controller.
	 * 
	 * @param service The service to manage the entries.
	 */
	@Autowired
	public DerivativeSourceAPIController(IDerivativeSourceService service) {
		super(service);
	}
}
