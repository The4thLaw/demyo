package org.demyo.service;

import java.util.List;

import org.demyo.model.Taxon;

/**
 * Service for management of {@link Taxon}s.
 */
public interface ITaxonService extends IModelService<Taxon>, IQuickSearchableService<Taxon> {
	/**
	 * Finds all tags. Does not resolve any lazy link, and does not paginate results, but loads the usage counts.
	 *
	 * @return The full list of tags.
	 */
	List<Taxon> findAllForIndex();

}
