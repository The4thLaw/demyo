package org.demyo.web.controller.api;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.demyo.model.Publisher;
import org.demyo.service.IPublisherService;

/**
 * Controller handling the API calls for {@link Publisher}s.
 */
@RestController
@RequestMapping("/api/publishers")
public class PublisherAPIController extends AbstractModelAPIController<Publisher> {
	private IPublisherService service;

	/**
	 * Creates the controller.
	 * 
	 * @param service The service to manage the entries.
	 */
	@Autowired
	public PublisherAPIController(IPublisherService service) {
		super(service);
		this.service = service;
	}

	@Override
	@GetMapping({ "/", "/index" })
	public MappingJacksonValue index(@RequestParam("view") Optional<String> view) {
		Iterable<Publisher> value = service.findAllForIndex();
		return getIndexView(view, value);
	}
}
