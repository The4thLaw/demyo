package org.demyo.dao;

import java.util.List;

import org.demyo.model.IModel;

import org.springframework.data.repository.NoRepositoryBean;

/**
 * Marker interface for services supporting quick search.
 * 
 * @param <M> The model type.
 */
@NoRepositoryBean
public interface IQuickSearchableRepo<M extends IModel> {
	/**
	 * Performs an exact match search on the specified query.
	 * 
	 * @param query The string to search for.
	 * @return The matches
	 */
	List<M> quickSearchExact(String query);

	/**
	 * Performs a LIKE match search on the specified query.
	 * 
	 * @param query The pattern to search for.
	 * @return The matches
	 */
	List<M> quickSearchLike(String query);
}
