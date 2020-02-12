package org.demyo.web.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.demyo.model.Publisher;
import org.demyo.service.IPublisherService;

/**
 * Controller handling the API calls for {@link Publisher}s.
 */
@RestController
@RequestMapping("/api/publishers")
public class PublisherAPIController extends AbstractModelAPIController<Publisher> {
	/**
	 * Creates the controller.
	 * 
	 * @param service The service to manage the entries.
	 */
	@Autowired
	public PublisherAPIController(IPublisherService service) {
		super(service);
	}
}
