package org.demyo.model.filters;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;

import org.demyo.model.Album;
import org.demyo.model.Binding;
import org.demyo.model.Collection;
import org.demyo.model.Publisher;
import org.demyo.model.QAlbum;
import org.demyo.model.Reader;
import org.demyo.model.Tag;

/**
 * An {@link IModelFilter} for {@link Album}s.
 */
public class AlbumFilter extends AbstractModelFilter<Album> {
	/**
	 * Factory method that creates a filter based on the {@link Publisher} internal ID.
	 * 
	 * @param The internal ID of the {@link Publisher}.
	 * @return the filter instance
	 */
	public static AlbumFilter forPublisher(long modelId) {
		AlbumFilter filter = new AlbumFilter();
		filter.publisher = modelId;
		return filter;
	}

	/**
	 * Factory method that creates a filter based on the {@link Binding} internal ID.
	 * 
	 * @param The internal ID of the {@link Binding}.
	 * @return the filter instance
	 */
	public static AlbumFilter forBinding(long modelId) {
		AlbumFilter filter = new AlbumFilter();
		filter.binding = modelId;
		return filter;
	}

	/** The internal ID of the {@link Publisher}. */
	private Long publisher;
	/** The internal ID of the {@link Collection}. */
	private Long collection;
	/** The internal ID of the {@link Binding}. */
	private Long binding;
	/** The internal ID of the {@link Tag}. */
	private Long tag;
	/** The internal ID of the {@link Reader} that has this {@link Album} as favourite. */
	private Long readerIdFavourite;
	/** The internal ID of the {@link Reader} that has this {@link Album} in their reading list. */
	private Long readerIdReadingList;

	@Override
	public Predicate getPredicate() {
		BooleanExpression e = null;

		if (publisher != null) {
			e = combine(e, QAlbum.album.publisher.id.eq(publisher));
		}
		if (collection != null) {
			e = combine(e, QAlbum.album.collection.id.eq(collection));
		}
		if (binding != null) {
			e = combine(e, QAlbum.album.binding.id.eq(binding));
		}
		if (tag != null) {
			e = combine(e, QAlbum.album.tags.any().id.eq(tag));
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
	 * Sets the internal ID of the {@link Publisher}.
	 *
	 * @param publisher the new internal ID of the {@link Publisher}
	 */
	public void setPublisher(Long publisher) {
		this.publisher = publisher;
	}

	/**
	 * Sets the internal ID of the {@link Collection}.
	 *
	 * @param collection the new internal ID of the {@link Collection}
	 */
	public void setCollection(Long collection) {
		this.collection = collection;
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
	 * Sets the internal ID of the {@link Tag}.
	 *
	 * @param tag the new internal ID of the {@link Tag}
	 */
	public void setTag(Long tag) {
		this.tag = tag;
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
