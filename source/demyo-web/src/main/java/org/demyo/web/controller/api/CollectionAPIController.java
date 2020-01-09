package org.demyo.web.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.demyo.model.Collection;
import org.demyo.service.ICollectionService;

@RestController
@RequestMapping("/api/collections")
public class CollectionAPIController extends AbstractModelAPIController<Collection> {
	private final ICollectionService service;

	@Autowired
	public CollectionAPIController(ICollectionService service) {
		super(Collection.class);
		this.service = service;
	}

	@Override
	protected ICollectionService getService() {
		return service;
	}

}
