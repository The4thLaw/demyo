package org.demyo.web.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.demyo.model.Universe;
import org.demyo.service.IUniverseService;

/**
 * Controller handling the API calls for {@link Universe}s.
 */
@RestController
@RequestMapping("/api/universes")
public class UniverseAPIController extends AbstractModelAPIController<Universe> {
	/**
	 * Creates the controller.
	 *
	 * @param service The service to manage the entries.
	 */
	@Autowired
	public UniverseAPIController(IUniverseService service) {
		super(service);
	}
}
