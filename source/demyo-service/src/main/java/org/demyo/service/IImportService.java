package org.demyo.service;

import java.io.InputStream;

import org.demyo.common.exception.DemyoException;
import org.demyo.service.importing.IImporter;

/**
 * Service to manage file imports.
 */
public interface IImportService {

	/**
	 * Registers an importer that supports a new file format.
	 * 
	 * @param importer The importer to register.
	 */
	void registerImporter(IImporter importer);

	/**
	 * Performs the import of a file, assuming a supporting importer has been registered.
	 * 
	 * @param originalFilename The original file name, as uploaded.
	 * @param content The uploaded content.
	 * @throws DemyoException In case of error during import.
	 */
	void importFile(String originalFilename, InputStream content) throws DemyoException;

}
