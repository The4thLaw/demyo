package org.demyo.web.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.demyo.model.Tag;
import org.demyo.service.ITagService;

/**
 * Controller handling the API calls for {@link Tag}s.
 */
@RestController
@RequestMapping("/api/tags")
public class TagAPIController extends AbstractModelAPIController<Tag> {
	/**
	 * Creates the controller.
	 * 
	 * @param service The service to manage the entries.
	 */
	@Autowired
	public TagAPIController(ITagService service) {
		super(service);
	}
}
