package org.demyo.service.importing;

import java.nio.file.Path;

import org.demyo.common.exception.DemyoException;

/**
 * Defines the contract for importers.
 * <p>
 * All importers must be stateless.
 * </p>
 */
public interface IImporter {
	/**
	 * Checks whether the importer supports the provided file.
	 * 
	 * @param originalFilename The file name, as uploaded.
	 * @param file The content of the uploaded file (name is meaningless).
	 * @return <code>true</code> if the importer supports this file. Else, <code>false</code>.
	 * @throws DemyoException In case of unrecoverable error during check.
	 */
	boolean supports(String originalFilename, Path file) throws DemyoException;

	/**
	 * Performs the import.
	 * 
	 * @param originalFilename The file name, as uploaded.
	 * @param file The content of the uploaded file (name is meaningless).
	 * @throws DemyoException In case of error during import.
	 */
	void importFile(String originalFilename, Path file) throws DemyoException;
}
