package org.demyo.web.controller;

import org.demyo.model.Tag;
import org.demyo.service.IModelServiceNG;
import org.demyo.service.ITagService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for {@link Tag} management.
 */
@Controller
@RequestMapping("/tags")
public class TagController extends AbstractModelControllerNG<Tag> {
	@Autowired
	private ITagService service;

	/**
	 * Default constructor.
	 */
	public TagController() {
		super(Tag.class, "tags", "tag");
	}

	@Override
	protected IModelServiceNG<Tag> getService() {
		return service;
	}
}
