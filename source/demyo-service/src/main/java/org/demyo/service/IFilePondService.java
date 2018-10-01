package org.demyo.service;

import java.io.IOException;
import java.io.InputStream;

/**
 * Service for backend integration of FilePond.
 * 
 * @since 2.1
 */
public interface IFilePondService {
	/**
	 * Stores the provided stream in a temporary location.
	 * 
	 * @param originalFileName The original filename for the file.
	 * @param input The stream to the file to store.
	 * @return A unique identifier for that file.
	 * @throws IOException In case of error while copying the file to a temporary location.
	 */
	String process(String originalFileName, InputStream input) throws IOException;

	/**
	 * Reverts the requested file from the temporary location.
	 * 
	 * @param id The identifier of the file to remove.
	 */
	void revert(String id);
}
