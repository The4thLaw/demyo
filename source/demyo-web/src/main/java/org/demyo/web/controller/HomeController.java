package org.demyo.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for the home page.
 */
@Controller
public class HomeController extends AbstractController {
	/**
	 * Displays the home page.
	 * 
	 * @param model The view model
	 * @return The view name
	 */
	@RequestMapping("/")
	public String viewHome(Model model) {
		suppressQuickSearch(model);
		return "core/home";
	}
}
