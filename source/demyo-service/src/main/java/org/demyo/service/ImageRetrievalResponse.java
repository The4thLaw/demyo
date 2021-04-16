package org.demyo.service;

import java.io.File;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

/**
 * Represents the results of the request for an image.
 */
public class ImageRetrievalResponse {
	private final File file;
	private boolean exact = true;

	/**
	 * Creates a response. By default, all responses are {@link #isExact() exact}
	 * 
	 * @param file The file containing the requested image.
	 */
	public ImageRetrievalResponse(File file) {
		this.file = file;
	}

	/**
	 * Gets a resource to access the image.
	 * 
	 * @return The resource.
	 */
	public Resource getResource() {
		return new FileSystemResource(file);
	}

	/**
	 * Sets a flag indicating whether the response is exact.
	 * 
	 * @param exact the flag.
	 */
	public void setExact(boolean exact) {
		this.exact = exact;
	}

	/**
	 * Checks whether an image response is exact. The response is exact if subsequent calls are expected to have the
	 * same result. Exact responses can be trusted and cached. Inexact responses shouldn't.
	 * 
	 * @return <code>true</code> if the response is exact.
	 */
	public boolean isExact() {
		return exact;
	}
}
