package org.demyo.model.util;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

import org.demyo.model.Album;

public class AlbumComparator implements Comparator<Album> {
	private final Collator collator;

	public AlbumComparator() {
		// This relies on the French locale, like in the database. Should not be an issue in English at least.
		collator = Collator.getInstance(Locale.FRENCH);
	}

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

		comparison = collator.compare(a1.getTitle(), a2.getTitle());
		if (comparison != 0) {
			return comparison;
		}
		// In case of equal everything, still distinguish them by ID to avoid omitting some results
		return a1.getId().compareTo(a2.getId());
	}

	private static <T> int nullSafeComparison(Comparable<T> c1, T c2) {
		if (c1 == null && c2 == null) {
			return 0;
		}
		if (c1 == null) {
			return -1;
		}
		if (c2 == null) {
			return 1;
		}
		return c1.compareTo(c2);
	}
}
