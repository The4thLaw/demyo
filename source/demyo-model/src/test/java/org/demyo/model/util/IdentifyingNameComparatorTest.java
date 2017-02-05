package org.demyo.model.util;

import org.demyo.model.Series;

import org.assertj.core.api.Assertions;
import org.junit.Test;

/**
 * Tests for {@link IdentifyingNameComparator}.
 */
public class IdentifyingNameComparatorTest {

	/**
	 * Main test.
	 */
	@Test
	public void test() {
		Series s1 = new Series();
		s1.setName("A");

		Series s2 = new Series();
		s2.setName("B");
		s2.setId(1L);

		Series s3 = new Series();
		s3.setName("B");
		s3.setId(1L);

		IdentifyingNameComparator comp = new IdentifyingNameComparator();

		Assertions.assertThat(comp.compare(s1, s2)).isLessThan(0);
		Assertions.assertThat(comp.compare(s2, s1)).isGreaterThan(0);
		Assertions.assertThat(comp.compare(s2, s3)).isEqualTo(0);
	}

}