package org.demyo.model.util;

import java.util.Comparator;

import org.demyo.model.Author;

/**
 * A {@link Comparator} allowing to sort {@link Author}s based on their name.
 */
public class AuthorComparator extends AbstractModelComparator<Author> {
	@Override
	public int compare(Author a1, Author a2) {
		int comparison;

		comparison = nullSafeCollatingComparison(a1.getName(), a2.getName());
		if (comparison != 0) {
			return comparison;
		}

		comparison = nullSafeCollatingComparison(a1.getFirstName(), a2.getFirstName());
		if (comparison != 0) {
			return comparison;
		}

		comparison = nullSafeCollatingComparison(a1.getNickname(), a2.getNickname());
		if (comparison != 0) {
			return comparison;
		}

		return defaultComparison(a1, a2);
	}
}
