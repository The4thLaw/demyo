package org.demyo.web.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.demyo.model.Collection;
import org.demyo.service.ICollectionService;

/**
 * Controller handling the API calls for {@link Collection}s.
 */
@RestController
@RequestMapping("/api/collections")
public class CollectionAPIController extends AbstractModelAPIController<Collection> {
	/**
	 * Creates the controller.
	 * 
	 * @param service The service to manage the entries.
	 */
	@Autowired
	public CollectionAPIController(ICollectionService service) {
		super(service);
	}
}
