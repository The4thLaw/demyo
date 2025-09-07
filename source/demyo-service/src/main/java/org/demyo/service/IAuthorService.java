package org.demyo.service;

import java.util.List;

import org.demyo.common.exception.DemyoException;
import org.demyo.model.Author;
import org.demyo.model.Taxon;
import org.demyo.model.beans.AuthorAlbums;

/**
 * Service for management of {@link Author}s.
 */
public interface IAuthorService extends IModelService<Author>, IQuickSearchableService<Author> {

	/**
	 * Finds all "real" authors, i.e. authors who are not a pseudonym of another.
	 *
	 * @param withPseudonyms Whether to include pseudonyms in the list of authors. Defaults to true.
	 * @return a list of authors
	 */
	List<Author> findAll(boolean withPseudonyms);

	/**
	 * Gets the albums to which a specific author participated.
	 *
	 * @param authorId The Author internal identifier
	 * @return The structured works
	 */
	AuthorAlbums getAuthorAlbums(long authorId);

	/**
	 * Recovers an image from FilePond and uses it for the specified Author.
	 *
	 * @param authorId The ID of the Author to change.
	 * @param mainFilePondId The FilePond ID of the image to recover.
	 * @throws DemyoException In case of error during recovery.
	 */
	void recoverFromFilePond(long authorId, String mainFilePondId) throws DemyoException;

	/*
	 * Gets the genres in which a specific author is active.
	 *
	 * @param id The Author internal identifier
	 * @return The genres.
	 */
    List<Taxon> getAuthorGenres(long id);

}
