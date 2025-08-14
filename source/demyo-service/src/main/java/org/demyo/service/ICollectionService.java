package org.demyo.service;

import java.util.List;

import org.demyo.common.exception.DemyoException;
import org.demyo.model.Collection;
import org.demyo.model.Publisher;

/**
 * Service for management of {@link Collection}s.
 */
public interface ICollectionService extends IModelService<Collection>, IQuickSearchableService<Collection> {
	/**
	 * Finds the {@link Collection}s belonging to a specific {@link Publisher}.
	 *
	 * @param publisherId The Publisher ID
	 * @return The associated Collections
	 */
	List<Collection> findByPublisherId(long publisherId);

	/**
	 * Recovers an image from FilePond and uses it for the specified Collection.
	 *
	 * @param collectionId The ID of the Collection to change.
	 * @param mainFilePondId The FilePond ID of the image to recover.
	 * @throws DemyoException In case of error during recovery.
	 */
	void recoverFromFilePond(long collectionId, String mainFilePondId) throws DemyoException;
}
