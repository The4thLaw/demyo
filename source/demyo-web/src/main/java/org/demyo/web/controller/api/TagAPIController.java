package org.demyo.web.controller.api;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.demyo.model.Tag;
import org.demyo.service.ITagService;

/**
 * Controller handling the API calls for {@link Tag}s.
 */
@RestController
@RequestMapping("/api/tags")
public class TagAPIController extends AbstractModelAPIController<Tag> {
	private ITagService service;

	/**
	 * Creates the controller.
	 * 
	 * @param service The service to manage the entries.
	 */
	@Autowired
	public TagAPIController(ITagService service) {
		super(service);
		this.service = service;
	}

	/**
	 * Retrieves the full list of the tags with the counts.
	 * 
	 * @param view The Jackson view to apply.
	 * @return The list.
	 */
	@Override
	@GetMapping({ "/", "/index" })
	public MappingJacksonValue index(@RequestParam("view") Optional<String> view) {
		List<Tag> value = service.findAllForIndex();
		return getIndexView(view, value);
	}
}
