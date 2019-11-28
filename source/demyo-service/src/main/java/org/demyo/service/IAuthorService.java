package org.demyo.service;

import org.demyo.model.Author;
import org.demyo.model.beans.AuthorAlbums;

/**
 * Service for management of {@link Author}s.
 */
public interface IAuthorService extends IModelService<Author>, IQuickSearchableService<Author> {

	/**
	 * Gets the albums to which a specific author participated.
	 * 
	 * @param authorId The Author internal identifier
	 * @return The structured works
	 */
	AuthorAlbums getAuthorAlbums(long authorId);

}
