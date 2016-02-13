package org.demyo.service.exporting;

import java.io.File;

import org.demyo.common.exception.DemyoException;

public interface IExporter {

	/**
	 * Exports the library to a temporary file.
	 * 
	 * @return The exported data
	 * @throws DemyoException In case of error during export.
	 */
	File export() throws DemyoException;

	/**
	 * Returns the expected extension of the exported file.
	 * 
	 * @param withResources Defines whether the extension applies to an export with or without resources.
	 * @return The extension (without heading ".")
	 */
	String getExtension(boolean withResources);
}
