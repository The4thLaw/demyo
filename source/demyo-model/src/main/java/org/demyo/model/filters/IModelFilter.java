package org.demyo.model.filters;

import com.querydsl.core.types.Predicate;

import org.demyo.model.IModel;

/**
 * Defines a filter for models of a given type.
 * 
 * @param <M> The model type.
 */
public interface IModelFilter<M extends IModel> {
	/**
	 * Gets the {@link Predicate} associated with this filter.
	 * 
	 * @return The predicate.
	 */
	Predicate getPredicate();
}
