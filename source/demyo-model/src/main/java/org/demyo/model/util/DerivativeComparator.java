package org.demyo.model.util;

import java.util.Comparator;

import org.demyo.model.Derivative;

/**
 * A {@link Comparator} that can sort Derivatives based on the Series and Album, then type.
 */
public class DerivativeComparator extends AbstractModelComparator<Derivative> {
	private static final long serialVersionUID = -6911490901403756503L;

	private AlbumAndSeriesComparator albumComparator = new AlbumAndSeriesComparator();
	private transient Comparator<Derivative> fallbackComparator = Comparator //
			.<Derivative, String>comparing(d -> d.getType().getName()) //
			.thenComparing(Derivative::getId);

	@Override
	public int compare(Derivative d1, Derivative d2) {
		int comparison = albumComparator.compare(d1.getAlbum(), d1.getSeries(), d2.getAlbum(), d2.getSeries());

		if (comparison != 0) {
			return comparison;
		}

		return fallbackComparator.compare(d1, d2);
	}

}
