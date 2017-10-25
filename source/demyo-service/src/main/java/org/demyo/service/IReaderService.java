package org.demyo.service;

import org.demyo.model.Reader;

/**
 * Service for management of {@link Reader}s.
 */
public interface IReaderService extends IModelService<Reader> {

	/**
	 * Gets the currently logged in {@link Reader}.
	 * 
	 * @return The currently logged in {@link Reader} (can be <code>null</code>).
	 */
	Reader getCurrentReader();

	/**
	 * Sets the current {@link Reader} in the context of the request.
	 * <p>
	 * This method should be called at least once per request.
	 * </p>
	 * 
	 * @param r The {@link Reader} (can be <code>null</code>).
	 */
	void setCurrentReader(Reader r);

}
