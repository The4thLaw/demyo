package org.demyo.model.filters;

import com.querydsl.core.types.dsl.BooleanExpression;

import org.demyo.model.IModel;

/**
 * Base class for implementations of {@link IModelFilter}.
 * 
 * @param <M> The model type.
 */
public abstract class AbstractModelFilter<M extends IModel> implements IModelFilter<M> {
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
