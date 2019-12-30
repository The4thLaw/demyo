package org.demyo.model.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a quick link in the header.
 */
public class HeaderLink {

	/** The link count. */
	private static int linkCount = 1;

	/** The URL for the link, relative to the application root. */
	private final String urlFromRoot;
	/** The icon specification. */
	private final String iconSpec;
	/** The label ID. */
	private final String label;
	/** The link ID. */
	private final String id;

	/**
	 * Default constructor. Sets a unique ID automatically.
	 * 
	 * @param urlFromRoot The URL for the link, relative to the application root
	 * @param iconSpec The icon specification
	 * @param label The label ID
	 */
	@JsonCreator
	public HeaderLink(@JsonProperty("urlFromRoot") String urlFromRoot, @JsonProperty("iconSpec") String iconSpec,
			@JsonProperty("label") String label) {
		this.urlFromRoot = urlFromRoot;
		this.iconSpec = iconSpec;
		this.label = label;
		this.id = "header_quick_link_" + linkCount++;
	}

	/**
	 * Gets the URL for the link, relative to the application root.
	 * 
	 * @return the URL for the link, relative to the application root
	 */
	public String getUrlFromRoot() {
		return urlFromRoot;
	}

	/**
	 * Gets the icon specification.
	 * 
	 * @return the icon specification
	 */
	public String getIconSpec() {
		return iconSpec;
	}

	/**
	 * Gets the label ID.
	 * 
	 * @return the label ID
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Gets the link ID.
	 * 
	 * @return the link ID
	 */
	public String getId() {
		return id;
	}
}
