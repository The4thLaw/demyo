package org.demyo.model.util;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

import org.demyo.model.IModel;

/**
 * A {@link Comparator} that sorts elements according to their {@link IModel#getIdentifyingName()} property.
 */
public class IdentifyingNameComparator implements Comparator<IModel> {
	private final Collator collator;

	public IdentifyingNameComparator() {
		// This relies on the French locale, like in the database. Should not be an issue in English at least.
		collator = Collator.getInstance(Locale.FRENCH);
	}

	@Override
	public int compare(IModel m1, IModel m2) {
		// TODO: make this locale-aware so that accented characters are properly sorted
		//return m1.getIdentifyingName().compareTo(m2.getIdentifyingName());
		return collator.compare(m1.getIdentifyingName(), m2.getIdentifyingName());
	}
}
