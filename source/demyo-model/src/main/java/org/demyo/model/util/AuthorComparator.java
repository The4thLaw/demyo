package org.demyo.model.util;

import java.util.Comparator;

import org.hibernate.Hibernate;

import org.demyo.model.Author;

/**
 * A {@link Comparator} allowing to sort {@link Author}s based on their name.
 */
public class AuthorComparator extends AbstractModelComparator<Author> {
	private static final long serialVersionUID = -3559656322037140067L;

	@Override
	public int compare(Author a1, Author a2) {
		// If authors aren't pseudonyms, we compare them as-is
		if (!isPseudonym(a1) && !isPseudonym(a2)) {
			return rawCompare(a1, a2);
		}

		Author ra1 = getRealAuthor(a1);
		Author ra2 = getRealAuthor(a2);

		// If they are both pseudonyms
		if (isPseudonym(a1) && isPseudonym(a2)) {
			// Either it's for the same real author, and we sort them by their pseudonym name
			if (a1.getPseudonymOf().getId().equals(a2.getPseudonymOf().getId())) {
				return rawCompare(a1, a2);
			}
			// Or the real authors are different, and we sort using those
			return rawCompare(ra1, ra2);
		}

		// At this stage one author is a pseudonym and the other isn't
		// The only edge case is that one is a pseudonym for the other, in which case the real one goes first
		if (isPseudonym(a1) && ra1.getId().equals(a2.getId())) {
			return 1;
		}
		if (isPseudonym(a2) && ra2.getId().equals(a1.getId())) {
			return -1;
		}

		// Now one is a pseudonym and the other isn't and neither are related. We sort using the real names
		// so that pseudonyms are grouped with their real authors
		return rawCompare(ra1, ra2);
	}

	private int rawCompare(Author a1, Author a2) {
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

	private static boolean isPseudonym(Author author) {
		return author.getPseudonymOf() != null && Hibernate.isInitialized(author.getPseudonymOf());
	}

	private static Author getRealAuthor(Author author) {
		if (isPseudonym(author)) {
			return author.getPseudonymOf();
		}
		return author;
	}
}
