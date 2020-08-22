package org.demyo.service;

import java.util.List;

import org.demyo.model.Tag;

/**
 * Service for management of {@link Tag}s.
 */
public interface ITagService extends IModelService<Tag>, IQuickSearchableService<Tag> {
	/**
	 * Finds all tags. Does not resolve any lazy link, and does not paginate results, but loads the usage counts.
	 * 
	 * @return The full list of tags.
	 */
	List<Tag> findAllForIndex();

}
