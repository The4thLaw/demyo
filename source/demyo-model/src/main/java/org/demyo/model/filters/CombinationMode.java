package org.demyo.model.filters;

/**
 * The way conditions of an {@link IModelFilter} should be combined.
 */
public enum CombinationMode {
	/** All of the filter items are applicable. */
	AND,
	/** Any of the filter item is applicable. */
	OR
}
