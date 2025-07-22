package org.demyo.model.util;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import org.demyo.model.Author;

/**
 * Tests for AuthorComparator.
 */
class AuthorComparatorTest {
	/**
	 * Test real authors.
	 */
	@Test
	void testReal() {
		Author a1 = getAuthor(1, "Philippe", "Buchet");
		Author a2 = getAuthor(2, "Jean-David", "Morvan");
		Author a3 = getAuthor(1, "Philippe", "Buchet");

		AuthorComparator comp = new AuthorComparator();

		Assertions.assertThat(comp.compare(a1, a2)).isNegative();
		Assertions.assertThat(comp.compare(a2, a1)).isPositive();
		Assertions.assertThat(comp.compare(a1, a3)).isZero();
	}

	/**
	 * Test pseudonyms having the same real author.
	 */
	@Test
	void testSameReal() {
		Author a1 = getAuthor(1, "Philippe", "Buchet");
		Author ap1 = getAuthor(2, "A1", "A1");
		ap1.setPseudonymOf(a1);
		Author ap2 = getAuthor(3, "A2", "A2");
		ap2.setPseudonymOf(a1);

		AuthorComparator comp = new AuthorComparator();

		Assertions.assertThat(comp.compare(a1, ap1)).isNegative();
		Assertions.assertThat(comp.compare(a1, ap2)).isNegative();
		Assertions.assertThat(comp.compare(ap1, a1)).isPositive();
		Assertions.assertThat(comp.compare(ap2, a1)).isPositive();
		Assertions.assertThat(comp.compare(ap1, ap2)).isNegative();
		Assertions.assertThat(comp.compare(ap2, ap1)).isPositive();
	}

	private Author getAuthor(long id, String fname, String lname) {
		Author author = new Author();
		author.setId(id);
		author.setFirstName(fname);
		author.setName(lname);
		return author;
	}
}
