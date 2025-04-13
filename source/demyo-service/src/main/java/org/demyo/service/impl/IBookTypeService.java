package org.demyo.service.impl;

import org.demyo.model.BookType;
import org.demyo.service.IModelService;

public interface IBookTypeService extends IModelService<BookType> {
	/**
	 * Checks if book type management has been explicitly enabled.
	 *
	 * @return <code>true</code> if it's enabled.
	 */
	boolean isManagementEnabled();

	/**
	 * Enables book type management.
	 */
	void enableManagement();

	/**
	 * Reassigns any album that used a book type to a different type.
	 *
	 * @param from The ID of the type to move
	 * @param to The ID of the type to use for the reassignment
	 */
	void reassign(long from, long to);
}
