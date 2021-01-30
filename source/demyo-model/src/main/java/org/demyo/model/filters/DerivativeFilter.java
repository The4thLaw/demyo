package org.demyo.model.filters;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;

import org.demyo.model.Album;
import org.demyo.model.Derivative;
import org.demyo.model.DerivativeSource;
import org.demyo.model.DerivativeType;
import org.demyo.model.QDerivative;
import org.demyo.model.Series;

/**
 * An {@link IModelFilter} for {@link Derivative}s.
 */
public class DerivativeFilter extends AbstractModelFilter {
	/**
	 * Factory method that creates a filter based on the {@link Series} internal ID.
	 * 
	 * @param The internal ID of the {@link Series}.
	 * @return the filter instance
	 */
	public static DerivativeFilter forSeries(long modelId) {
		DerivativeFilter filter = new DerivativeFilter();
		filter.series = modelId;
		return filter;
	}

	/**
	 * Factory method that creates a filter based on the {@link Album} internal ID.
	 * 
	 * @param The internal ID of the {@link Album}.
	 * @return the filter instance
	 */
	public static DerivativeFilter forAlbum(long modelId) {
		DerivativeFilter filter = new DerivativeFilter();
		filter.album = modelId;
		return filter;
	}

	/**
	 * Factory method that creates a filter based on the {@link org.demyo.model.Author artist} internal ID.
	 * 
	 * @param The internal ID of the {@link org.demyo.model.Author artist}.
	 * @return the filter instance
	 */
	public static DerivativeFilter forArtist(long modelId) {
		DerivativeFilter filter = new DerivativeFilter();
		filter.artist = modelId;
		return filter;
	}

	/**
	 * Factory method that creates a filter based on the {@link DerivativeType} internal ID.
	 * 
	 * @param The internal ID of the {@link DerivativeType}.
	 * @return the filter instance
	 */
	public static DerivativeFilter forType(long modelId) {
		DerivativeFilter filter = new DerivativeFilter();
		filter.type = modelId;
		return filter;
	}

	/**
	 * Factory method that creates a filter based on the {@link DerivativeSource} internal ID.
	 * 
	 * @param The internal ID of the {@link DerivativeSource}.
	 * @return the filter instance
	 */
	public static DerivativeFilter forSource(long modelId) {
		DerivativeFilter filter = new DerivativeFilter();
		filter.source = modelId;
		return filter;
	}

	/** The internal ID of the {@link Series}. */
	private Long series;
	/** The internal ID of the {@link Album}. */
	private Long album;
	/** The internal ID of the {@link org.demyo.model.Author artist}. */
	private Long artist;
	/** The internal ID of the {@link DerivativeType}. */
	private Long type;
	/** The internal ID of the {@link DerivativeSource}. */
	private Long source;

	@Override
	public Predicate toPredicate() {
		BooleanExpression e = null;

		if (series != null) {
			e = combine(e, QDerivative.derivative.series.id.eq(series));
		}
		if (album != null) {
			e = combine(e, QDerivative.derivative.album.id.eq(album));
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
	 * Sets the internal ID of the {@link Album}.
	 *
	 * @param album the new internal ID of the {@Album}.
	 */
	public void setAlbum(Long album) {
		this.album = album;
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
