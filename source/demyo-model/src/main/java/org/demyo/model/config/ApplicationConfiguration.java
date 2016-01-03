package org.demyo.model.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.demyo.common.config.SystemConfiguration;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import com.fasterxml.jackson.databind.type.CollectionType;

/**
 * Demyo application configuration. These are things that the user can change through the application and impact
 * its visible behaviour.
 * 
 * @see SystemConfiguration
 * @author $Author: xr $
 * @version $Revision: 1073 $
 */
public class ApplicationConfiguration {
	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationConfiguration.class);

	/** The language in which the application is displayed. */
	private final Locale language;
	/** The list of quick links in the header. */
	private final List<HeaderLink> headerLinks;
	/** The number of items per page of textual entries. */
	private final int pageSizeForText;
	/** The number of items per page of images. */
	private final int pageSizeForImages;
	/** The number of series per page of albums. */
	private final int pageSizeForAlbums;
	/** The maximum thumbnail width in pixels. */
	private final int thumbnailWidth;
	/** The maximum thumbnail height in pixels. */
	private final int thumbnailHeight;

	/**
	 * Creates an application configuration from a configuration file.
	 * 
	 * @param config The configuration to retrieve values from.
	 */
	public ApplicationConfiguration(Configuration config) {
		language = Locale.forLanguageTag(config.getString("language"));
		pageSizeForText = config.getInt("paging.textPageSize");
		pageSizeForImages = config.getInt("paging.imagePageSize");
		pageSizeForAlbums = config.getInt("paging.albumPageSize");
		thumbnailWidth = config.getInt("thumbnail.width");
		thumbnailHeight = config.getInt("thumbnail.height");

		// Load the header links as JSON data
		String headerLinksSpec = config.getString("header.quickLinks", "[]");
		JsonFactory jsonFactory = new JsonFactory();
		ObjectMapper jsonMapper = new ObjectMapper(jsonFactory);
		CollectionType jsonType = jsonMapper.getTypeFactory().constructCollectionType(ArrayList.class,
				HeaderLink.class);
		List<HeaderLink> links = new ArrayList<>();
		try {
			LOGGER.debug("Header links: {}", headerLinksSpec);
			JsonParser jsonParser = jsonFactory.createParser(headerLinksSpec);
			links = jsonMapper.<List<HeaderLink>> readValue(jsonParser, jsonType);
		} catch (RuntimeJsonMappingException | IOException e) {
			LOGGER.warn("Failed to load the header configuration", e);
		}
		headerLinks = links;
	}

	/**
	 * Stores all business values from this class in the given configuration.
	 * 
	 * @param config The configuration to fill.
	 */
	public void fillConfiguration(Configuration config) {
		config.setProperty("language", language.toString());
		config.setProperty("paging.textPageSize", pageSizeForText);
		config.setProperty("paging.imagePageSize", pageSizeForImages);
		config.setProperty("thumbnail.width", thumbnailWidth);
		config.setProperty("thumbnail.height", thumbnailHeight);
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
