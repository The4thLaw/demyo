package org.demyo.model.util;

import org.demyo.model.Album;
import org.demyo.model.Series;

/**
 * An {@link AlbumComparator} that also considers series.
 */
public class AlbumAndSeriesComparator extends AlbumComparator {
	@Override
	public int compare(Album a1, Album a2) {
		Series s1 = a1.getSeries();
		Series s2 = a2.getSeries();

		if (s1 != null && s2 != null) {
			if (s1.getId().equals(s2.getId())) {
				// Same series, use the regular comparison
				return super.compare(a1, a2);
			}
		}

		// Different series, or null series name. Compare by title.
		String title1 = s1 != null ? s1.getName() : a1.getTitle();
		String title2 = s2 != null ? s2.getName() : a2.getTitle();

		int comparison = title1.compareTo(title2);

		if (comparison == 0) {
			return defaultComparison(a1, a2);
		}
		return comparison;
	}
}
