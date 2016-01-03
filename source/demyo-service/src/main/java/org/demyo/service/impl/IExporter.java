package org.demyo.service.impl;

import java.io.File;

public interface IExporter {

	/**
	 * Exports the library to a temporary file.
	 * 
	 * @return The exported data
	 */
	File export();

	/**
	 * Returns the expected extension of the exported file.
	 * 
	 * @return The extension (without heading ".")
	 */
	String getExtension();

}
