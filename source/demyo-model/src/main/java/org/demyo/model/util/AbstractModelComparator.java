package org.demyo.model.util;

import java.io.Serializable;
import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

import org.demyo.model.IModel;

/**
 * Base comparison class for {@link IModel}s.
 *
 * @param <M> The model type.
 */
public abstract class AbstractModelComparator<M extends IModel> implements Comparator<M>, Serializable {
	private static final long serialVersionUID = 6870681552272458176L;

	/**
	 * Performs a nullsafe comparison (according to their compare methods) of the provided elements. A {@literal null}
	 * element is considered as smaller than a non-{@literal null} one.
	 *
	 * @param <T> The type of item to compare.
	 * @param c1 The first element for comparison
	 * @param c2 The second element for comparison
	 * @return a negative integer, zero, or a positive integer as the first object is less than, equal to, or greater
	 *         than the second object.
	 */
	public static <T> int nullSafeComparison(Comparable<T> c1, T c2) {
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

	private final transient Collator collator;

	/**
	 * The Constructor.
	 */
	protected AbstractModelComparator() {
		// This relies on the French locale, like in the database. Should not be an issue in English at least.
		collator = Collator.getInstance(Locale.FRENCH);
	}

	/**
	 * Fall-back method for comparison of models, all relevant fields being equal.
	 *
	 * @param m1 The first model for comparison.
	 * @param m2 The second model for comparison.
	 * @return a negative integer, zero, or a positive integer as the first model is less than, equal to, or greater
	 *         than the second model.
	 */
	protected final int defaultComparison(M m1, M m2) {
		// These ones are for very specific edge cases, like when you have an album named like a series
		if (m1 == null && m2 == null) {
			return 0;
		}
		if (m1 == null) {
			return -1;
		}
		if (m2 == null) {
			return 1;
		}
		// In case of equal everything, still distinguish them by ID to avoid omitting some results
		return m1.getId().compareTo(m2.getId());
	}

	/**
	 * Performs a null-safe, collation-aware comparison of the provided strings. A {@literal null} string is considered
	 * as smaller than a non-{@literal null} one.
	 *
	 * @param s1 The first string for comparison
	 * @param s2 The second string for comparison
	 * @return a negative integer, zero, or a positive integer as the first string is less than, equal to, or string
	 *         than the second object.
	 */
	protected int nullSafeCollatingComparison(String s1, String s2) {
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
