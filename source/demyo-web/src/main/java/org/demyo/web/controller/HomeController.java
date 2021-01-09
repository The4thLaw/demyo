package org.demyo.web.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import org.demyo.common.config.SystemConfiguration;

/**
 * Controller for the home, manifest and about pages.
 */
@Controller
public class HomeController extends AbstractController {
	private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);

	@Autowired
	private MessageSource messageSource;

	@Value("#{servletContext.contextPath}")
	private String servletContextPath;

	@Value("classpath*:public/js/app.*.js")
	private Resource[] appJs;
	private String appJsFilename;

	@Value("classpath*:public/css/app.*.css")
	private Resource[] appCss;
	private String appCssFilename;

	@Value("classpath*:public/js/chunk-vendors.*.js")
	private Resource[] vendorJs;
	private String vendorJsFilename;

	@Value("classpath*:public/js/app-legacy.*.js")
	private Resource[] appLegacyJs;
	private String appLegacyJsFilename;

	@Value("classpath*:public/js/chunk-vendors-legacy.*.js")
	private Resource[] vendorLegacyJs;
	private String vendorLegacyJsFilename;

	@Value("classpath*:public/css/chunk-vendors.*.css")
	private Resource[] vendorCss;
	private String vendorCssFilename;

	private final String appVersion;
	private final String appCodename;

	public HomeController() {
		this.appVersion = SystemConfiguration.getInstance().getVersion();
		this.appCodename = SystemConfiguration.getInstance().getCodename();
	}

	@PostConstruct
	private void init() {
		appJsFilename = getFrontendResource(appJs, "app.*.js");
		appCssFilename = getFrontendResource(appCss, "app.*.css");
		vendorJsFilename = getFrontendResource(vendorJs, "chunk-vendors.*.js");
		appLegacyJsFilename = getFrontendResource(appLegacyJs, "app-legacy.*.js");
		vendorLegacyJsFilename = getFrontendResource(vendorLegacyJs, "chunk-vendors-legacy.*.js");
		vendorCssFilename = getFrontendResource(vendorCss, "chunk-vendors.*.css");
	}

	/**
	 * Displays the home page.
	 * 
	 * @param model The view model
	 * @return The view name
	 */
	@GetMapping({
			// Index
			"/",
			// Static pages
			"/about",
			// Management
			"/manage/**",
			// Model pages (except images, which have special rules)
			"/albums", "/authors", "/bindings", "/collections", "/derivativeSources",
			"/derivativeTypes", "/derivatives", "/publishers", "/readers", "/series", "/tags", //
			"/albums/**", "/authors/**", "/bindings/**", "/collections/**", "/derivativeSources/**",
			"/derivativeTypes/**", "/derivatives/**", "/publishers/**", "/readers/**", "/series/**", "/tags/**",
			// Images
			"/images", "/images/", "/images/*/view", "/images/view/**", "/images/*/edit", "/images/new"
	})

	public String index(Model model) {
		LOGGER.trace("Accessing the home page");

		model.addAttribute("appVersion", appVersion);
		model.addAttribute("appCodename", appCodename);

		model.addAttribute("appJsFilename", appJsFilename);
		model.addAttribute("appCssFilename", appCssFilename);
		model.addAttribute("vendorJsFilename", vendorJsFilename);
		model.addAttribute("appLegacyJsFilename", appLegacyJsFilename);
		model.addAttribute("vendorLegacyJsFilename", vendorLegacyJsFilename);
		model.addAttribute("vendorCssFilename", vendorCssFilename);

		return "index";
	}

	private static String getFrontendResource(Resource[] resources, String name) {
		if (resources.length < 1) {
			LOGGER.warn("Failed to find the resource named '{}'; this is only fine during development");
		} else if (resources.length > 1) {
			throw new IllegalArgumentException(
					"Could not find a unique resource for " + name + ":" + Arrays.asList(resources));
		}
		return resources[0].getFilename();
	}

	/**
	 * Displays a Web Application Manifest.
	 * 
	 * @return The manifest map.
	 * @see https://developer.mozilla.org/en-US/docs/Web/Manifest
	 */
	@GetMapping("/manifest.json")
	@ResponseBody
	public Map<String, Object> getApplicationManifest(@RequestParam(name = "lang", required = false) Locale lang) {
		if (lang == null) {
			lang = Locale.getDefault();
		}
		// Note: Eventually, we could generate this on the client side :
		// https://medium.com/@alshakero/how-to-setup-your-web-app-manifest-dynamically-using-javascript-f7fbee899a61

		Map<String, Object> manifest = new HashMap<>();
		manifest.put("name", "Demyo");
		manifest.put("short_name", "Demyo");
		manifest.put("background_color", "#2196f3");
		manifest.put("theme_color", "#2196f3");
		manifest.put("description", messageSource.getMessage("app.description", null, lang));
		manifest.put("display", "standalone");
		manifest.put("lang", lang.toLanguageTag());
		manifest.put("orientation", "portrait-primary");
		manifest.put("start_url", servletContextPath);
		manifest.put("icons",
				Arrays.asList(getManifestIcon(16), getManifestIcon(32), getManifestIcon(48), getManifestIcon(64),
						getManifestIcon(144), getManifestIcon(192), getManifestIcon(196), getManifestIcon(270),
						getManifestIcon(558)));
		return manifest;
	}

	private Map<String, Object> getManifestIcon(int size) {
		Map<String, Object> icon = new HashMap<>();
		icon.put("src", servletContextPath + "/icons/demyo-" + size + ".png");
		icon.put("sizes", size + "x" + size);
		icon.put("type", "image/png");
		return icon;
	}
}
