package org.demyo.web.controller.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import org.demyo.model.ModelView;
import org.demyo.service.ISearchService;
import org.demyo.service.SearchResult;

/**
 * Controller for management of search operations.
 */
@RestController
@RequestMapping("/api/search")
public class SearchAPIController {
	private ISearchService service;

	/**
	 * Creates the controller.
	 * 
	 * @param service The service to perform searches.
	 */
	public SearchAPIController(ISearchService service) {
		this.service = service;
	}

	/**
	 * Performs a quick search.
	 * 
	 * @param query The pattern to search for
	 * @return The search results.
	 */
	@GetMapping("/quick")
	@JsonView(ModelView.Basic.class)
	public SearchResult quickSearch(@RequestParam("q") String query) {
		return service.quickSearch(query);
	}
}
