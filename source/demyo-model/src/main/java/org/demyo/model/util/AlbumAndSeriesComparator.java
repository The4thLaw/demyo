package org.demyo.model.util;

import org.demyo.model.Album;
import org.demyo.model.Series;

/**
 * An {@link AlbumComparator} that also considers series.
 */
public class AlbumAndSeriesComparator extends AlbumComparator {
	private static final long serialVersionUID = -7116259624751687861L;

	@Override
	public int compare(Album a1, Album a2) {
		Series s1 = a1.getSeries();
		Series s2 = a2.getSeries();

		return compare(a1, s1, a2, s2);
	}

	/**
	 * Compares two Albums without making assumptions about where the Series for the Albums can be found.
	 * 
	 * @param a1 The first Album.
	 * @param s1 The Series for the first Album.
	 * @param a2 The second Album.
	 * @param s2 The Series for the second Album.
	 * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater
	 *         than the second.
	 */
	protected int compare(Album a1, Series s1, Album a2, Series s2) {
		if (s1 != null && s2 != null && s1.getId().equals(s2.getId())) {
			// Same series, use the regular comparison
			return super.compare(a1, a2);
		}

		// Different series, or null series name. Compare by title.
		String title1 = s1 != null ? s1.getName() : a1.getTitle();
		String title2 = s2 != null ? s2.getName() : a2.getTitle();

		int comparison = title1.compareTo(title2);

		// Different Series but same name. Very weird case so provide a deterministic fallback
		if (comparison == 0) {
			return defaultComparison(a1, a2);
		}
		return comparison;
	}
}
