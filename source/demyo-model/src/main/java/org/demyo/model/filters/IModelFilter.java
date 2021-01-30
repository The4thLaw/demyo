package org.demyo.model.filters;

import com.querydsl.core.types.Predicate;

/**
 * Defines a filter for models of a given type.
 */
public interface IModelFilter {
	/**
	 * Converts this filter to a QueryDSL {@link Predicate}.
	 * 
	 * @return the predicate.
	 */
	Predicate toPredicate();
}
