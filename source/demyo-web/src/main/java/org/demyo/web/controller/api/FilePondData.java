package org.demyo.web.controller.api;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents data coming from a FilePond submission.
 */
class FilePondData {
	/**
	 * Identifiers for "other" images.
	 */
	@JsonProperty("filePondOtherImages")
	private String[] otherImages;

	public String[] getOtherImages() {
		return otherImages;
	}

	public void setOtherImages(String[] otherImages) {
		this.otherImages = otherImages;
	}
}
