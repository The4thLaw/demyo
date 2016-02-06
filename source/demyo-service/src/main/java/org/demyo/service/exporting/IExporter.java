package org.demyo.service.exporting;

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
	 * @param withResources Defines whether the extension applies to an export with or without resources.
	 * @return The extension (without heading ".")
	 */
	String getExtension(boolean withResources);
}
