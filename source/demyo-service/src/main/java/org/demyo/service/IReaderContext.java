package org.demyo.service;

import org.demyo.model.Reader;

/**
 * Provides methods to access the currently selected {@link Reader}.
 */
public interface IReaderContext {
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
	 * @param r The {@link Reader}. Must not be <code>null</code>.
	 */
	void setCurrentReader(Reader r);

	/**
	 * Clears the current {@link Reader}, and any cache related to it.
	 */
	void clearCurrentReader();
}
