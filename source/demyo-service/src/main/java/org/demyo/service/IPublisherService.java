package org.demyo.service;

import java.util.List;

import org.demyo.common.exception.DemyoException;
import org.demyo.model.Publisher;

/**
 * Service for management of {@link Publisher}s.
 */
public interface IPublisherService extends IModelService<Publisher>, IQuickSearchableService<Publisher> {

	/**
	 * Finds all {@link Publisher}s for display in an index page, with the suitable links initialized.
	 *
	 * @return The list of {@link Publisher}s
	 */
	List<Publisher> findAllForIndex();

	/**
	 * Recovers an image from FilePond and uses it for the specified Publisher.
	 *
	 * @param publisherId The ID of the Publisher to change.
	 * @param mainFilePondId The FilePond ID of the image to recover.
	 * @throws DemyoException In case of error during recovery.
	 */
	void recoverFromFilePond(long publisherId, String mainFilePondId) throws DemyoException;
}
