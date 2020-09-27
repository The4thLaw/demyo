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
	 * Converts this filter to a QueryDSL {@link Predicate}.
	 * 
	 * @return the predicate.
	 */
	Predicate toPredicate();
}
