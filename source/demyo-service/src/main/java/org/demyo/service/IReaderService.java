package org.demyo.service;

import org.demyo.model.Reader;

/**
 * Service for management of {@link Reader}s.
 */
public interface IReaderService extends IModelService<Reader> {
	/**
	 * Checks if the provided Reader exists.
	 * 
	 * @param readerId The Reader identifier to check.
	 * @return <code>true</code> iif the reader exists.
	 */
	boolean readerExists(long readerId);

	/**
	 * Gets the only Reader from the database.If there is not exactly one Reader, return <code>null</code>.
	 * 
	 * @return The unique Reader, or <code>null</code>.
	 */
	Reader getUniqueReader();

}
