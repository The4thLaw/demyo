package org.demyo.web.controller;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.demyo.service.IConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller for the home, manifest and about pages.
 */
@Controller
public class HomeController extends AbstractController {
	@Autowired
	private MessageSource messageSource;
	@Autowired
	private IConfigurationService config;

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

	/**
	 * Displays the about page.
	 * 
	 * @param model The view model
	 * @return The view name
	 */
	@RequestMapping("/about")
	public String viewAbout(HttpServletRequest request, Model model) {
		suppressQuickSearch(model);
		model.addAttribute("javaVersion", System.getProperty("java.version"));
		model.addAttribute("javaVendor", System.getProperty("java.vendor"));
		model.addAttribute("osName", System.getProperty("os.name"));
		model.addAttribute("osVersion", System.getProperty("os.version"));
		model.addAttribute("osArch", System.getProperty("os.arch"));
		model.addAttribute("userAgent", request.getHeader("User-Agent"));
		return "core/about";
	}

	/**
	 * Displays a Web Application Manifest.
	 * 
	 * @return The manifest map.
	 * @see https://developer.mozilla.org/en-US/docs/Web/Manifest
	 */
	@RequestMapping("/manifest.json")
	@ResponseBody
	public Map<String, Object> getApplicationManifest() {
		Locale language = config.getConfiguration().getLanguage();

		Map<String, Object> manifest = new HashMap<>();
		manifest.put("name", "Demyo");
		manifest.put("short_name", "Demyo");
		manifest.put("background_color", "#2196f3");
		manifest.put("theme_color", "#2196f3");
		manifest.put("description", messageSource.getMessage("app.description", null, language));
		manifest.put("display", "standalone");
		manifest.put("lang", language.toLanguageTag());
		manifest.put("orientation", "portrait-primary");
		manifest.put("start_url", "/");
		return manifest;
	}
}
