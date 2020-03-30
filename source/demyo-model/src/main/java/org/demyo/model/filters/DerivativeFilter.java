package org.demyo.model.filters;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;

import org.demyo.model.Derivative;
import org.demyo.model.DerivativeSource;
import org.demyo.model.DerivativeType;
import org.demyo.model.QDerivative;
import org.demyo.model.Series;

/**
 * An {@link IModelFilter} for {@link Derivative}s.
 */
public class DerivativeFilter extends AbstractModelFilter<Derivative> {
	/** The internal ID of the {@link Series}. */
	private Long series;
	/** The internal ID of the {@link org.demyo.model.Author artist}. */
	private Long artist;
	/** The internal ID of the {@link DerivativeType}. */
	private Long type;
	/** The internal ID of the {@link DerivativeSource}. */
	private Long source;

	@Override
	public Predicate getPredicate() {
		BooleanExpression e = null;

		if (series != null) {
			e = combine(e, QDerivative.derivative.series.id.eq(series));
		}
		if (artist != null) {
			e = combine(e, QDerivative.derivative.artist.id.eq(artist));
		}
		if (type != null) {
			e = combine(e, QDerivative.derivative.type.id.eq(type));
		}
		if (source != null) {
			e = combine(e, QDerivative.derivative.source.id.eq(source));
		}

		return e;
	}

	/**
	 * Sets the internal ID of the {@link Series}.
	 *
	 * @param series the new internal ID of the {@link Series}
	 */
	public void setSeries(Long series) {
		this.series = series;
	}

	/**
	 * Sets the internal ID of the {@link org.demyo.model.Author artist}.
	 *
	 * @param artist the new internal ID of the {@link org.demyo.model.Author artist}.
	 */
	public void setArtist(Long artist) {
		this.artist = artist;
	}

	/**
	 * Sets the internal ID of the {@link DerivativeType}.
	 *
	 * @param type the new internal ID of the {@link DerivativeType}
	 */
	public void setType(Long type) {
		this.type = type;
	}

	/**
	 * Sets the internal ID of the {@link DerivativeSource}.
	 *
	 * @param source the new internal ID of the {@link DerivativeSource}
	 */
	public void setSource(Long source) {
		this.source = source;
	}

}
