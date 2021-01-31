package org.demyo.web.controller.api;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller handling the API calls for everything related to the about page.
 */
@RestController
@RequestMapping("/api/about/")
public class AboutAPIController {
	/**
	 * Gets all relevant environment parameters
	 * 
	 * @return The environment.
	 */
	@GetMapping("/environment")
	public Map<String, String> getEnvironment() {
		Map<String, String> env = new HashMap<>();
		env.put("javaVersion", System.getProperty("java.version"));
		env.put("javaVendor", System.getProperty("java.vendor"));
		env.put("osName", System.getProperty("os.name"));
		env.put("osVersion", System.getProperty("os.version"));
		env.put("osArch", System.getProperty("os.arch"));
		return env;
	}
}
