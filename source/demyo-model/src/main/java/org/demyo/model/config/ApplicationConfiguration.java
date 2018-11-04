package org.demyo.model.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import com.fasterxml.jackson.databind.type.CollectionType;

import org.demyo.common.config.SystemConfiguration;

/**
 * Demyo application configuration. These are things that the user can change through the application and impact its
 * visible behaviour.
 * 
 * @see SystemConfiguration
 */
public class ApplicationConfiguration {
	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationConfiguration.class);

	private static final Locale SYSTEM_LOCALE = Locale.getDefault();

	private static final int DEFAULT_PAGE_SIZE_TEXT = 60;
	private static final int DEFAULT_PAGE_SIZE_IMAGES = 15;
	private static final int DEFAULT_PAGE_SIZE_ALBUMS = 20;
	private static final int DEFAULT_THUMB_WIDTH = 220;
	private static final int DEFAULT_THUMB_HEIGHT = 200;

	/**
	 * Gets a default configuration with default values.
	 * 
	 * @return the default configuration.
	 */
	public static ApplicationConfiguration getDefaultConfiguration() {
		ApplicationConfiguration config = new ApplicationConfiguration();

		config.setLanguage(SYSTEM_LOCALE);
		config.headerLinksSpec = "[]";
		config.headerLinks = new ArrayList<>();
		config.pageSizeForText = DEFAULT_PAGE_SIZE_TEXT;
		config.pageSizeForImages = DEFAULT_PAGE_SIZE_IMAGES;
		config.pageSizeForAlbums = DEFAULT_PAGE_SIZE_ALBUMS;
		config.thumbnailWidth = DEFAULT_THUMB_WIDTH;
		config.thumbnailHeight = DEFAULT_THUMB_HEIGHT;

		return config;
	}

	/** The language in which the application is displayed. */
	private Locale language;
	/** The specification for list of quick links in the header. */
	private String headerLinksSpec;
	/** The list of quick links in the header. */
	private List<HeaderLink> headerLinks;
	/** The number of items per page of textual entries. */
	private int pageSizeForText;
	/** The number of items per page of images. */
	private int pageSizeForImages;
	/** The number of series per page of albums. */
	private int pageSizeForAlbums;
	/** The maximum thumbnail width in pixels. */
	private int thumbnailWidth;
	/** The maximum thumbnail height in pixels. */
	private int thumbnailHeight;

	/**
	 * Default construtor with no values.
	 */
	private ApplicationConfiguration() {
	}

	/**
	 * Creates an application configuration from a configuration file.
	 * 
	 * @param config The configuration to retrieve values from.
	 */
	public ApplicationConfiguration(Map<String, String> config) {
		merge(config);
	}

	/**
	 * Merges the current application configuration with the provided one.
	 * 
	 * @param config The configuration to retrieve values from.
	 */
	public void merge(Map<String, String> config) {
		String languageCode = getString(config, "language");
		Locale theLanguage;
		if (languageCode != null) {
			theLanguage = Locale.forLanguageTag(languageCode);
		} else {
			theLanguage = Locale.getDefault();
		}
		setLanguage(theLanguage);

		pageSizeForText = getInt(config, "paging.textPageSize");
		pageSizeForImages = getInt(config, "paging.imagePageSize");
		pageSizeForAlbums = getInt(config, "paging.albumPageSize");
		thumbnailWidth = getInt(config, "thumbnail.width");
		thumbnailHeight = getInt(config, "thumbnail.height");

		// Load the header links as JSON data
		headerLinksSpec = getString(config, "header.quickLinks", "[]");
		JsonFactory jsonFactory = new JsonFactory();
		ObjectMapper jsonMapper = new ObjectMapper(jsonFactory);
		CollectionType jsonType = jsonMapper.getTypeFactory().constructCollectionType(ArrayList.class,
				HeaderLink.class);
		List<HeaderLink> links = new ArrayList<>();
		try {
			LOGGER.debug("Header links: {}", headerLinksSpec);
			JsonParser jsonParser = jsonFactory.createParser(headerLinksSpec);
			links = jsonMapper.<List<HeaderLink>>readValue(jsonParser, jsonType);
		} catch (RuntimeJsonMappingException | IOException e) {
			LOGGER.warn("Failed to load the header configuration", e);
		}
		headerLinks = links;
	}

	private static String getString(Map<String, String> config, String key) {
		return getString(config, key, null);
	}

	private static String getString(Map<String, String> config, String key, String defaultValue) {
		String value = config.get(key);
		if (value == null) {
			value = defaultValue;
		}
		return value;
	}

	private static int getInt(Map<String, String> config, String key) {
		return Integer.valueOf(config.get(key));
	}

	/**
	 * Returns a map of all values as strings.
	 * 
	 * @return The map.
	 */
	public SortedMap<String, String> asMap() {
		SortedMap<String, String> config = new TreeMap<>();

		config.put("language", language.toString());
		config.put("paging.textPageSize", String.valueOf(pageSizeForText));
		config.put("paging.imagePageSize", String.valueOf(pageSizeForImages));
		config.put("paging.albumPageSize", String.valueOf(pageSizeForAlbums));
		config.put("thumbnail.width", String.valueOf(thumbnailWidth));
		config.put("thumbnail.height", String.valueOf(thumbnailHeight));
		config.put("header.quickLinks", headerLinksSpec);

		return config;
	}

	/**
	 * Gets the language in which the application is displayed.
	 * 
	 * @return the language in which the application is displayed
	 */
	public Locale getLanguage() {
		return language;
	}

	/**
	 * Sets the language in which the application is displayed.
	 * 
	 * @param language the new language in which the application is displayed
	 */
	public void setLanguage(Locale language) {
		this.language = language;

		// This won't impact the JVM locale (used notably for javax.validation messages), but we can't really do that
		// in a multi-reader context (this would result in an edit war between readers).
		LocaleContextHolder.setLocale(language);
	}

	/**
	 * Gets the list of quick links in the header.
	 * 
	 * @return the list of quick links in the header
	 */
	public List<HeaderLink> getHeaderLinks() {
		return headerLinks;
	}

	/**
	 * Gets the number of items per page of textual entries.
	 * 
	 * @return the number of items per page of textual entries
	 */
	public int getPageSizeForText() {
		return pageSizeForText;
	}

	/**
	 * Gets the number of items per page of images.
	 * 
	 * @return the number of items per page of images
	 */
	public int getPageSizeForImages() {
		return pageSizeForImages;
	}

	/**
	 * Gets the number of series per page of albums.
	 * 
	 * @return the number of series per page of albums
	 */
	public int getPageSizeForAlbums() {
		return pageSizeForAlbums;
	}

	/**
	 * Gets the maximum thumbnail width in pixels.
	 * 
	 * @return the maximum thumbnail width in pixels
	 */
	public int getThumbnailWidth() {
		return thumbnailWidth;
	}

	/**
	 * Gets the maximum thumbnail height in pixels.
	 * 
	 * @return the maximum thumbnail height in pixels
	 */
	public int getThumbnailHeight() {
		return thumbnailHeight;
	}
}
