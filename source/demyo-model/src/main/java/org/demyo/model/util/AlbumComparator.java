package org.demyo.model.util;

import java.util.Comparator;

import org.demyo.model.Album;
import org.demyo.model.Series;

/**
 * A {@link Comparator} allowing to sort {@link Album}s based on their order in a specific {@link Series}.
 */
public class AlbumComparator extends AbstractModelComparator<Album> {
	private static final long serialVersionUID = 4093459448029858856L;

	@Override
	public int compare(Album a1, Album a2) {
		int comparison;

		comparison = nullSafeComparison(a1.getCycle(), a2.getCycle());
		if (comparison != 0) {
			return comparison;
		}

		comparison = nullSafeComparison(a1.getNumber(), a2.getNumber());
		if (comparison != 0) {
			return comparison;
		}

		comparison = nullSafeComparison(a1.getNumberSuffix(), a2.getNumberSuffix());
		if (comparison != 0) {
			return comparison;
		}

		// If both albums have dates, sort them that way. If not, there's not enough information to make guesses, so
		// leave them by title
		if (a1.getFirstEditionDate() != null && a2.getFirstEditionDate() != null) {
			comparison = nullSafeComparison(a1.getFirstEditionDate(), a2.getFirstEditionDate());
			if (comparison != 0) {
				return comparison;
			}
		}
		if (a1.getCurrentEditionDate() != null && a2.getCurrentEditionDate() != null) {
			comparison = nullSafeComparison(a1.getCurrentEditionDate(), a2.getCurrentEditionDate());
			if (comparison != 0) {
				return comparison;
			}
		}

		comparison = nullSafeCollatingComparison(a1.getTitle(), a2.getTitle());
		if (comparison != 0) {
			return comparison;
		}

		return defaultComparison(a1, a2);
	}
}
