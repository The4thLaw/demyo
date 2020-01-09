package org.demyo.web.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.demyo.model.Publisher;
import org.demyo.service.IPublisherService;

@RestController
@RequestMapping("/api/publishers")
public class PublisherAPIController extends AbstractModelAPIController<Publisher> {
	private final IPublisherService service;

	@Autowired
	public PublisherAPIController(IPublisherService service) {
		super(Publisher.class);
		this.service = service;
	}

	@Override
	protected IPublisherService getService() {
		return service;
	}

}
