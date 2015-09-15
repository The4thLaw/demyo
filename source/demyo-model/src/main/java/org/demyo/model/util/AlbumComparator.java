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
		// TODO: order by cycle, number, number_suffix, title
		int comparison;
		comparison = collator.compare(a1.getIdentifyingName(), a2.getIdentifyingName());
		if (comparison != 0) {
			return comparison;
		}
		// In case of equal everything, still distinguish them by ID to avoid omitting some results
		return a1.getId().compareTo(a2.getId());
	}
}
