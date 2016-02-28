package org.demyo.web.controller;

import org.demyo.service.impl.ISearchService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for management of search operations.
 */
@Controller
@RequestMapping("/search")
public class SearchController extends AbstractController {
	@Autowired
	private ISearchService searchService;

	/**
	 * Performs a quick search.
	 * 
	 * @param query The pattern to search for
	 * @param model The {@link Model}
	 * @return The view name
	 */
	@RequestMapping("/quick")
	public String quickSearch(@RequestParam("q") String query, Model model) {
		model.addAttribute("results", searchService.quickSearch(query));
		setLayoutPlain(model);
		return "search/quick";
	}
}
