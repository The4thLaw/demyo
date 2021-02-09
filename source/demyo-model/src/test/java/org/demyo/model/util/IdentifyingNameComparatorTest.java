package org.demyo.model.util;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import org.demyo.model.Series;

/**
 * Tests for {@link IdentifyingNameComparator}.
 */
public class IdentifyingNameComparatorTest {

	/**
	 * Main test.
	 */
	@Test
	void test() {
		Series s1 = new Series();
		s1.setName("A");

		Series s2 = new Series();
		s2.setName("B");
		s2.setId(1L);

		Series s3 = new Series();
		s3.setName("B");
		s3.setId(1L);

		IdentifyingNameComparator comp = new IdentifyingNameComparator();

		Assertions.assertThat(comp.compare(s1, s2)).isNegative();
		Assertions.assertThat(comp.compare(s2, s1)).isPositive();
		Assertions.assertThat(comp.compare(s2, s3)).isZero();
	}

}
