package org.demyo.web.controller.api;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.demyo.service.ITranslationService;

/**
 * REST API controller for everything related to translations.
 */
@RestController
@RequestMapping("/api/translations/")
public class TranslationAPIController {
	@Autowired
	private ITranslationService service;

	/**
	 * Gets all translated labels in a given language.
	 * 
	 * @param lang The language to process.
	 * @return The translated labels.
	 */
	@GetMapping({ "/{lang}", "/{lang}/" })
	public Map<String, String> getTranslations(@PathVariable("lang") String lang) {
		return service.getAllTranslations(lang);
	}
}
