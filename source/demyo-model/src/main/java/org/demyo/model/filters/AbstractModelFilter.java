package org.demyo.model.filters;

import com.querydsl.core.types.dsl.BooleanExpression;

/**
 * Base class for implementations of {@link IModelFilter}.
 */
public abstract class AbstractModelFilter implements IModelFilter {
	/** The combination mode for this filter. */
	private CombinationMode mode;

	protected BooleanExpression combine(BooleanExpression base, BooleanExpression toCombine) {
		if (base == null) {
			return toCombine;
		}

		if (mode == CombinationMode.AND) {
			return base.and(toCombine);
		} else {
			return base.or(toCombine);
		}
	}

	public CombinationMode getMode() {
		return mode;
	}

	public void setMode(CombinationMode mode) {
		this.mode = mode;
	}
}
