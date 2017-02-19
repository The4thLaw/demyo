package org.demyo.service;

/**
 * Defines various methods to search for models.
 */
public interface ISearchService {

	/**
	 * Performs a quick search based on the specified query.
	 * <p>
	 * The semantics of the model search depends on the searched model. It can be on a Series or Album title,
	 * Author name, etc.
	 * </p>
	 * 
	 * @param query The query string (a part of the model's name).
	 * @return The found matches.
	 */
	SearchResult quickSearch(String query);

}
