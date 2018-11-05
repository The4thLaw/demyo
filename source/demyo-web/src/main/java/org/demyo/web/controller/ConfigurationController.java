package org.demyo.web.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.demyo.model.config.ApplicationConfiguration;
import org.demyo.service.IConfigurationService;
import org.demyo.service.IReaderContext;
import org.demyo.service.IReaderService;

/**
 * Controller for {@link ApplicationConfiguration} management.
 */
@Controller
@RequestMapping("/configuration")
public class ConfigurationController extends AbstractController {
	@Autowired
	private IConfigurationService service;
	@Autowired
	private IReaderService readerService;

	/**
	 * Views the advanced configuration page.
	 * 
	 * @param model The view model.
	 * @return The view name.
	 */
	@GetMapping("/advanced")
	public String viewAdvanced(Model model) {
		// Configuration is already set by parent controller
		return "configuration/advanced";
	}

	/**
	 * Saves the configuration from the advanced page.
	 * 
	 * @param model The view model.
	 * @param newConfig The configuration to save.
	 * @return The view name.
	 */
	@PostMapping("/advanced")
	public String saveAdvance(Model model, @RequestParam Map<String, String> newConfig) {
		IReaderContext context = readerService.getContext();
		service.save(newConfig, context.getCurrentReader());
		context.clearCurrentReader();
		return redirect(model, "/configuration/advanced");
	}
}
