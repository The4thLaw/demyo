package org.demyo.web.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.demyo.model.Tag;
import org.demyo.service.ITagService;

@RestController
@RequestMapping("/api/tags")
public class TagAPIController extends AbstractModelAPIController<Tag> {
	private final ITagService service;

	@Autowired
	public TagAPIController(ITagService service) {
		super(Tag.class);
		this.service = service;
	}

	@Override
	protected ITagService getService() {
		return service;
	}

}
