package org.demyo.web.controller.api;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents data coming from a FilePond submission.
 */
class FilePondData {
	/**
	 * Identifier for the "main" image.
	 */
	@JsonProperty("filePondMainImage")
	private String mainImage;

	/**
	 * Identifiers for "other" images.
	 */
	@JsonProperty("filePondOtherImages")
	private String[] otherImages;

	/**
	 * Gets the identifier for the "main" image.
	 *
	 * @return the identifier for the "main" image
	 */
	public String getMainImage() {
		return mainImage;
	}

	/**
	 * Sets the identifier for the "main" image.
	 *
	 * @param mainImage the new identifier for the "main" image
	 */
	public void setMainImage(String mainImage) {
		this.mainImage = mainImage;
	}

	/**
	 * Gets the identifiers for "other" images.
	 *
	 * @return the identifiers for "other" images
	 */
	public String[] getOtherImages() {
		return otherImages;
	}

	/**
	 * Sets the identifiers for "other" images.
	 *
	 * @param otherImages the new identifiers for "other" images
	 */
	public void setOtherImages(String[] otherImages) {
		this.otherImages = otherImages;
	}
}
