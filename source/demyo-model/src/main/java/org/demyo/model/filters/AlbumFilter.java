package org.demyo.model.filters;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;

import org.demyo.model.Album;
import org.demyo.model.Binding;
import org.demyo.model.QAlbum;
import org.demyo.model.Reader;

/**
 * An {@link IModelFilter} for {@link Album}s.
 */
public class AlbumFilter extends AbstractModelFilter<Album> {
	/** The internal ID of the {@link Binding}. */
	private Long binding;
	/** The internal ID of the {@link Reader} that has this {@link Album} as favourite. */
	private Long readerIdFavourite;
	/** The internal ID of the {@link Reader} that has this {@link Album} in their reading list. */
	private Long readerIdReadingList;

	@Override
	public Predicate getPredicate() {
		BooleanExpression e = null;

		if (binding != null) {
			e = combine(e, QAlbum.album.binding.id.eq(binding));
		}
		if (readerIdFavourite != null) {
			e = combine(e, QAlbum.album.readersFavourites.any().id.eq(readerIdFavourite)
					.or(QAlbum.album.series.readersFavourites.any().id.eq(readerIdFavourite)));
		}
		if (readerIdReadingList != null) {
			e = combine(e, QAlbum.album.readersReadingList.any().id.eq(readerIdReadingList));
		}

		return e;
	}

	/**
	 * Sets the internal ID of the {@link Binding}.
	 *
	 * @param binding the {@link Binding} ID
	 */
	public void setBinding(Long binding) {
		this.binding = binding;
	}

	/**
	 * Sets the internal ID of the {@link Reader} that has this {@link Album} as favourite.
	 *
	 * @param readerIdFavourite the {@link Reader} ID
	 */
	public void setReaderIdFavourite(Long readerIdFavourite) {
		this.readerIdFavourite = readerIdFavourite;
	}

	/**
	 * Sets the internal ID of the {@link Reader} that has this {@link Album} in their reading list.
	 *
	 * @param readerIdReadingList the {@link Reader} ID
	 */
	public void setReaderIdReadingList(Long readerIdReadingList) {
		this.readerIdReadingList = readerIdReadingList;
	}
}
