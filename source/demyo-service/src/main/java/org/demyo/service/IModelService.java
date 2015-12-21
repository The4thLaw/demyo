package org.demyo.service;

import java.util.List;

import org.demyo.model.IModel;

import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort.Order;

/**
 * This interface defines a common set of operation on models.
 * 
 * @param <M>
 */
public interface IModelService<M extends IModel> {

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
	 * Finds all entities from this model that match the given criteria. Does not resolve any lazy link, and does
	 * not paginate results.
	 * 
	 * @param criterion A potential criterion to restrict the set of entities. May be <code>null</code> for no
	 *        restriction.
	 * @return The list of entities.
	 */
	// If needed use QueryDSL, see JpaSpecificationExecutor
	//List<M> findAll(Criterion criterion);

	/**
	 * Finds the list of entities for the given page for a given criterion. The number of entities per page is
	 * defined by the service, depending on the configuration.
	 * 
	 * @param currentPage The page number (starting at 1).
	 * @param orders Ordering of the result set. May be <code>null</code> to use the default ordering. If no
	 *        default ordering is defined, the ordering is defined by the database.
	 * @return The list of entities.
	 */
	Slice<M> findPaginated(int currentPage, Order... orders);

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
