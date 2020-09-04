package org.demyo.service;

import java.util.List;

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

}
