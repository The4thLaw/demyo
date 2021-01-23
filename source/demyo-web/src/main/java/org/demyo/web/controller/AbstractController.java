package org.demyo.web.controller;

import javax.activation.MimetypesFileTypeMap;

/**
 * Base controller for Demyo.
 */
public abstract class AbstractController {
	protected MimetypesFileTypeMap mimeTypes = new MimetypesFileTypeMap();

	/**
	 * Default constructor.
	 */
	protected AbstractController() {
		// May be missing from some systems at least
		mimeTypes.addMimeTypes("image/png png");
		mimeTypes.addMimeTypes("image/jpeg jpg");
		mimeTypes.addMimeTypes("image/jpeg jpeg");

		// Note: intentionally, there is no specific MIME type for DEA export files.
		// The HTTP spec discourages vendor-specific MIME types
	}

}
