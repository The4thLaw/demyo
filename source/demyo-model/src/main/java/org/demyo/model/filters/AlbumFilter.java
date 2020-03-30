package org.demyo.model.filters;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;

import org.demyo.model.Album;
import org.demyo.model.Binding;
import org.demyo.model.QAlbum;

/**
 * An {@link IModelFilter} for {@link Album}s.
 */
public class AlbumFilter extends AbstractModelFilter<Album> {
	/** The internal ID of the {@link Binding}. */
	private Long binding;

	@Override
	public Predicate getPredicate() {
		BooleanExpression e = null;

		if (binding != null) {
			e = combine(e, QAlbum.album.binding.id.eq(binding));
		}

		return e;
	}

	/**
	 * Sets the internal ID of the {@link Binding}.
	 *
	 * @param binding the new internal ID of the {@link Binding}
	 */
	public void setBinding(Long binding) {
		this.binding = binding;
	}
}
