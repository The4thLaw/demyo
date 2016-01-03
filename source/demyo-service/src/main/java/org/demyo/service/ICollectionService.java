package org.demyo.service;

import java.util.List;

import org.demyo.model.Collection;
import org.demyo.model.Publisher;

/**
 * Service for management of {@link Collection}s.
 */
public interface ICollectionService extends IModelService<Collection> {
	/**
	 * Finds the {@link Collection}s belonging to a specific {@link Publisher}.
	 * 
	 * @param publisherId The Publisher ID
	 * @return The associated Collections
	 */
	public List<Collection> findByPublisherId(long publisherId);
}
