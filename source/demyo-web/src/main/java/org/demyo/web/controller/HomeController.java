package org.demyo.web.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.IntStream;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
import org.demyo.web.config.NoncedCSPHeaderWriter;

/**
 * Controller for the home, manifest and about pages.
 */
@Controller
public class HomeController extends AbstractController {
	private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);
	private static final String MODEL_KEY_VERSION = "appVersion";
	private static final String MODEL_KEY_CODENAME = "appCodename";

	@Autowired
	private MessageSource messageSource;

	@Value("#{servletContext.contextPath}")
	private String servletContextPath;

	@Value("classpath*:public/assets/*.js")
	private Resource[] allJsResources;
	private String indexJsFilename;
	private List<String> otherJsFilenames;

	@Value("classpath*:public/assets/*.css")
	private Resource[] allCssResources;
	private List<String> cssFilenames;

	private final String appVersion;
	private final String appCodename;

	public HomeController() {
		this.appVersion = SystemConfiguration.getInstance().getVersion();
		this.appCodename = SystemConfiguration.getInstance().getCodename();
	}

	private static Comparator<Resource> indexPositionComparator(int position) {
		return (r1, r2) -> {
			String name1 = r1.getFilename();
			String name2 = r2.getFilename();
			if (name1.startsWith("index")) {
				return position;
			}
			if (name2.startsWith("index")) {
				return -position;
			}
			if (!name1.startsWith("@vendor") && name2.startsWith("@vendor")) {
				return position;
			}
			if (name1.startsWith("@vendor") && !name2.startsWith("@vendor")) {
				return -position;
			}
			return name1.compareTo(name2);
		};
	}

	private static Comparator<Resource> indexFirst() {
		return indexPositionComparator(-1);
	}

	private static Comparator<Resource> indexLast() {
		return indexPositionComparator(1);
	}

	@PostConstruct
	private void init() {
		List<Resource> allJs = new ArrayList<>(List.of(allJsResources));
		allJs.sort(indexFirst());
		indexJsFilename = allJs.get(0).getFilename();
		otherJsFilenames = allJs.subList(1, allJs.size()).stream().map(Resource::getFilename).toList();

		List<Resource> allCss = new ArrayList<>(List.of(allCssResources));
		allCss.sort(indexLast());
		cssFilenames = allCss.stream().map(Resource::getFilename).toList();

		LOGGER.trace("Index JS resource: {}", indexJsFilename);
		LOGGER.trace("Sorted non-index JS resources: {}", otherJsFilenames);
		LOGGER.trace("Sorted CSS resources: {}", cssFilenames);
	}

	/**
	 * Displays the home page.
	 *
	 * @param model The view model
	 * @return The view name
	 */
	@GetMapping({"/", "{_:^(?!api|favicon).*$}", "{_:^(?!api|assets|icons).*$}/**"})
	public String index(Model model, HttpServletRequest request, HttpServletResponse response) {
		LOGGER.trace("Accessing the home page");

		model.addAttribute(MODEL_KEY_VERSION, appVersion);
		model.addAttribute(MODEL_KEY_CODENAME, appCodename);

		model.addAttribute("indexJsFilename", indexJsFilename);
		model.addAttribute("otherJsFilenames", otherJsFilenames);
		model.addAttribute("cssFilenames", cssFilenames);

		// The fact that this is a JSP request means that the headers and
		// attributes are altered. So the standard header writer attributes
		// are not properly exposed. We thus need to call the writer ourselves.
		// Not very clean, but the alternative of switching to something like
		// Thymeleaf is even worse.
		new NoncedCSPHeaderWriter().writeHeaders(request, response);

		return "index";
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
		manifest.put("start_url", servletContextPath + "/");
		manifest.put("icons",
			IntStream.of(16, 32, 48, 64, 144, 192, 196, 270, 558).mapToObj(this::getManifestIcon).toList());
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
