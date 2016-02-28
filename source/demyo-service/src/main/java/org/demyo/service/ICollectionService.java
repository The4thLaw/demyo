package org.demyo.service;

import java.util.List;

import org.demyo.model.Collection;
import org.demyo.model.Publisher;
import org.demyo.service.impl.IQuickSearchableService;

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
}
