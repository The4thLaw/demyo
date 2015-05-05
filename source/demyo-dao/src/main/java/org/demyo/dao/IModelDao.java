package org.demyo.dao;

import java.util.List;

import org.demyo.model.IModel;
import org.demyo.model.util.PaginatedList;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

/**
 * Base interface for CRUD operations on models.
 * 
 * @author $Author: xr $
 * @version $Revision: 1071 $
 * @param <M>
 */
public interface IModelDao<M extends IModel> {
	/**
	 * Finds an entity based on its ID.
	 * 
	 * @param id The ID of the entity.
	 * @param loadLazy Whether to load (<code>true</code>) the first level of lazy associations or not (
	 *        <code>false</code>).
	 * @return The found entity.
	 */
	M findById(long id, boolean loadLazy);

	/**
	 * Searches for the page number of the first page to contain an item starting with a given character.
	 * 
	 * @param startsWith The character which the page should be the first to contain.
	 * @param pageSize The size of the page in number of entities (starting at 1).
	 * @return The page number.
	 */
	int getFirstPageForLetter(char startsWith, int pageSize);

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
	List<M> findAll(Criterion criterion);

	/**
	 * Saves the given entity in the database.
	 * 
	 * @param model The entity to save.
	 * @return The ID of the saved entity.
	 */
	long save(M model);

	/**
	 * Deletes the given entity from the database.
	 * 
	 * @param id The ID of the entity to delete.
	 */
	void delete(long id);

	/**
	 * Finds a list of entities, with paging and no specific criterion, association fetching strategy or ordering.
	 * 
	 * @param currentPage The current page number (starting at 1).
	 * @param pageSize The size of the page in number of entities (starting at 1). Default ordering is used: the
	 *        ordering is defined by model or, if not, by the database.
	 * @return The list of matching entities, with pagination information.
	 * @see #findPaginated(int, int, Criterion, JoinTypeHolder, Order...)
	 */
	PaginatedList<M> findPaginated(int currentPage, int pageSize);

	/**
	 * Finds a list of entities, with paging and no specific association fetching strategy or ordering.
	 * 
	 * @param currentPage The current page number (starting at 1).
	 * @param pageSize The size of the page in number of entities (starting at 1).
	 * @param criterion A potential criterion to restrict the set of entities. May be <code>null</code> for no
	 *        restriction.
	 * @return The list of matching entities, with pagination information.
	 * @see #findPaginated(int, int, Criterion, JoinTypeHolder, Order...)
	 */
	PaginatedList<M> findPaginated(int currentPage, int pageSize, Criterion criterion);

	/**
	 * Finds a list of entities, with paging and no specific association fetching strategy.
	 * 
	 * @param currentPage The current page number (starting at 1).
	 * @param pageSize The size of the page in number of entities (starting at 1).
	 * @param criterion A potential criterion to restrict the set of entities. May be <code>null</code> for no
	 *        restriction.
	 * @param orders Ordering of the result set. May be <code>null</code> to use the default ordering. If no
	 *        default ordering is defined, the ordering is defined by the database.
	 * @return The list of matching entities, with pagination information.
	 * @see #findPaginated(int, int, Criterion, JoinTypeHolder, Order...)
	 */
	PaginatedList<M> findPaginated(int currentPage, int pageSize, Criterion criterion, Order... orders);

	/**
	 * Finds a list of entities, with paging.
	 * 
	 * @param currentPage The current page number (starting at 1).
	 * @param pageSize The size of the page in number of entities (starting at 1).
	 * @param criterion A potential criterion to restrict the set of entities. May be <code>null</code> for no
	 *        restriction.
	 * @param fetchModes Potential fetching of associations without relying on the Model's default strategy. May be
	 *        <code>null</code> for no special behaviour.
	 * @param orders Ordering of the result set. May be <code>null</code> to use the default ordering. If no
	 *        default ordering is defined, the ordering is defined by the database.
	 * @return The list of matching entities, with pagination information.
	 */
	PaginatedList<M> findPaginated(int currentPage, int pageSize, Criterion criterion, JoinTypeHolder fetchModes,
			Order... orders);
}
