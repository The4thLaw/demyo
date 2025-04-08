package org.demyo.model.beans;

import java.text.Collator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.context.i18n.LocaleContextHolder;

import com.fasterxml.jackson.annotation.JsonView;

import org.demyo.model.Album;
import org.demyo.model.ModelView.Basic;
import org.demyo.model.Series;
import org.demyo.model.util.AlbumComparator;

/**
 * Represents a logical series, i.e. either a real series or a one-shot album.
 */
public class MetaSeries implements Comparable<MetaSeries> {
	@JsonView(Basic.class)
	private final Series series;
	@JsonView(Basic.class)
	private final Album album;
	@JsonView(Basic.class)
	private final SortedSet<Album> albums = new TreeSet<>(new AlbumComparator());

	public MetaSeries(Album album) {
		if (album.getSeries() != null) {
			this.series = album.getSeries();
			this.album = null;
			addAlbum(album);
		} else {
			this.series = null;
			this.album = album;
		}
	}

	public String getTitle() {
		return isActualSeries() ? series.getName() : album.getTitle();
	}

	public boolean isActualSeries() {
		return this.series != null;
	}

	public void addAlbum(Album a) {
		albums.add(a);
	}

	private long getId() {
		return isActualSeries() ? series.getId() : -album.getId();
	}

	@Override
	public int compareTo(MetaSeries o) {
		if (o == null) {
			return -1;
		}
		int comp = Collator.getInstance(LocaleContextHolder.getLocale()).compare(getTitle(), o.getTitle());
		if (comp != 0) {
			return comp;
		}
		return Long.compare(getId(), o.getId());
	}

	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		if (!other.getClass().equals(getClass())) {
			return false;
		}

		MetaSeries otherMeta = (MetaSeries) other;

		if ((series != null && otherMeta.series == null)
			|| (series == null && otherMeta.series != null)
			|| (album != null && otherMeta.album == null)
			|| (album == null && otherMeta.album != null)) {
			return false;
		}

		EqualsBuilder builder = new EqualsBuilder();
		builder.append(getTitle(), otherMeta.getTitle());
		if (series != null) {
			builder.append(series.getId(), otherMeta.series.getId());
		}
		if (album != null) {
			builder.append(album.getId(), otherMeta.album.getId());
		}

		return builder.build();
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		if (series != null) {
			builder.append(series.getId());
		}
		if (album != null) {
			builder.append(album.getId());
		}
		return builder.build();
	}
}
