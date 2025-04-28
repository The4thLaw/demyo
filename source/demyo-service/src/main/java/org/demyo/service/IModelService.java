package org.demyo.service;

import java.util.List;

import org.demyo.model.IModel;

/**
 * This interface defines a common set of operation on models.
 *
 * @param <M> The model type.
 */
public interface IModelService<M extends IModel> {

	/**
	 * Counts the total number of entities.
	 *
	 * @return The number of entities.
	 */
	long count();

	/**
	 * Finds an entity based on its ID in order to view it.
	 *
	 * @param id The ID of the entity.
	 * @return The found entity.
	 */
	M getByIdForView(long id);

	/**
	 * Finds an entity based on its ID in order to edit it.
	 *
	 * @param id The ID of the entity.
	 * @return The found entity.
	 */
	M getByIdForEdition(long id);

	/**
	 * Searches for the page number of the first page to contain an item starting with a given character.
	 *
	 * @param startsWith The character which the page should be the first to contain.
	 * @return The page number.
	 */
	int getFirstPageForLetter(char startsWith);

	/**
	 * Finds all entities from this model. Does not resolve any lazy link, and does not paginate results.
	 *
	 * @return The full list of entities.
	 */
	List<M> findAll();

	/**
	 * Saves the given model.
	 *
	 * @param model The model to save.
	 * @return The ID of the saved entity.
	 */
	long save(M model);

	/**
	 * Deletes the given entity from the database.
	 *
	 * @param id The ID of the entity to delete.
	 */
	void delete(long id);
}
