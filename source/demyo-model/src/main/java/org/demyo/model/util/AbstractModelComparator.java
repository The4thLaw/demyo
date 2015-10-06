package org.demyo.model.util;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

import org.demyo.model.IModel;

/**
 * Base comparison class for {@link IModel}s.
 * 
 * @param <M> The model type.
 */
// TODO: Javadoc
public abstract class AbstractModelComparator<M extends IModel> implements Comparator<M> {
	protected static <T> int nullSafeComparison(Comparable<T> c1, T c2) {
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

	private final Collator collator;

	/**
	 * The Constructor.
	 */
	public AbstractModelComparator() {
		// This relies on the French locale, like in the database. Should not be an issue in English at least.
		collator = Collator.getInstance(Locale.FRENCH);
	}

	protected final Collator getCollator() {
		return collator;
	}

	protected final int defaultComparison(M m1, M m2) {
		// In case of equal everything, still distinguish them by ID to avoid omitting some results
		return m1.getId().compareTo(m2.getId());
	}

	protected <T> int nullSafeCollatingComparison(String s1, String s2) {
		if (s1 == null && s2 == null) {
			return 0;
		}
		if (s1 == null) {
			return -1;
		}
		if (s2 == null) {
			return 1;
		}
		return collator.compare(s1, s2);
	}

}
