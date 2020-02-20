package org.demyo.model.filters;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;

import org.demyo.model.Derivative;
import org.demyo.model.DerivativeSource;
import org.demyo.model.DerivativeType;
import org.demyo.model.QDerivative;

/**
 * An {@link IModelFilter} for {@link Derivative}s.
 */
public class DerivativeFilter extends AbstractModelFilter<Derivative> {
	/** The internal ID of the {@link org.demyo.model.Author artist}. */
	private Long artist;
	/** The internal ID of the {@link DerivativeType}. */
	private Long type;
	/** The internal ID of the {@link DerivativeSource}. */
	private Long source;

	@Override
	public Predicate getPredicate() {
		BooleanExpression e = null;

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
	 * Gets the internal ID of the {@link org.demyo.model.Author artist}.
	 *
	 * @return the internal ID of the {@link org.demyo.model.Author artist}.
	 */
	public Long getArtist() {
		return artist;
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
	 * Gets the internal ID of the {@link DerivativeType}.
	 *
	 * @return the internal ID of the {@link DerivativeType}
	 */
	public Long getType() {
		return type;
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
	 * Gets the internal ID of the {@link DerivativeSource}.
	 *
	 * @return the internal ID of the {@link DerivativeSource}
	 */
	public Long getSource() {
		return source;
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
