package org.demyo.model.beans;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonView;

import org.demyo.model.Album;
import org.demyo.model.ModelView.Basic;
import org.demyo.model.Series;
import org.demyo.model.util.AlbumComparator;

/**
 * Represents a logical series, i.e. either a real series or a one-shot album.
 */
// TODO [Vue]: rename this class, remove the other
public class MetaSeriesNG implements Comparable<MetaSeriesNG> {
	private static final Comparator<MetaSeriesNG> COMPARATOR = Comparator.comparing(MetaSeriesNG::getTitle)
			.thenComparing(MetaSeriesNG::getId);

	@JsonView(Basic.class)
	private final Series series;
	@JsonView(Basic.class)
	private final Album album;
	@JsonView(Basic.class)
	private final SortedSet<Album> albums = new TreeSet<Album>(new AlbumComparator());

	public MetaSeriesNG(Album album) {
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
	public int compareTo(MetaSeriesNG o) {
		if (o == null) {
			return -1;
		}
		return COMPARATOR.compare(this, o);
	}
}
