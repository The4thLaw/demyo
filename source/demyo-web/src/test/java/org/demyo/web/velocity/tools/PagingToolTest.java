package org.demyo.web.velocity.tools;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link PagingTool}.
 */
public class PagingToolTest {
	private final PagingTool tool = new PagingTool();

	/**
	 * Tests {@link PagingTool#getFirstLetter(String)}.
	 */
	@Test
	public void testFirstLetter() {
		Assert.assertEquals('A', tool.getFirstLetter("Albert"));
		Assert.assertEquals('E', tool.getFirstLetter("Éric"));
		Assert.assertEquals('#', tool.getFirstLetter("@xel"));
		Assert.assertEquals('#', tool.getFirstLetter("4xel"));
	}

	/**
	 * Tests {@link PagingTool#getPageNumbers(int, int)}.
	 */
	@Test
	public void testPageNumbers() {
		// CHECKSTYLE:OFF
		// Start
		Assert.assertEquals(Arrays.asList(1), tool.getPageNumbers(1, 1));
		Assert.assertEquals(Arrays.asList(1, 2), tool.getPageNumbers(1, 2));
		Assert.assertEquals(Arrays.asList(1, 2, 3), tool.getPageNumbers(1, 3));
		Assert.assertEquals(Arrays.asList(1, 2, 3, 4), tool.getPageNumbers(1, 4));
		Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5), tool.getPageNumbers(1, 5));
		Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6), tool.getPageNumbers(1, 6));
		Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6, 7), tool.getPageNumbers(1, 7));
		Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 6, 7, 8), tool.getPageNumbers(1, 8));
		Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 7, 8, 9), tool.getPageNumbers(1, 9));

		// Moving to the middle
		Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 48, 49, 50), tool.getPageNumbers(1, 50));
		Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5, 48, 49, 50), tool.getPageNumbers(2, 50));
		Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6, 48, 49, 50), tool.getPageNumbers(3, 50));
		Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 48, 49, 50), tool.getPageNumbers(4, 50));
		Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 48, 49, 50), tool.getPageNumbers(5, 50));
		Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 48, 49, 50), tool.getPageNumbers(6, 50));
		Assert.assertEquals(Arrays.asList(1, 2, 4, 5, 6, 7, 8, 9, 10, 48, 49, 50), tool.getPageNumbers(7, 50));
		Assert.assertEquals(Arrays.asList(1, 2, 5, 6, 7, 8, 9, 10, 11, 48, 49, 50), tool.getPageNumbers(8, 50));
		Assert.assertEquals(Arrays.asList(1, 2, 22, 23, 24, 25, 26, 27, 28, 48, 49, 50),
				tool.getPageNumbers(25, 50));

		// End
		Assert.assertEquals(Arrays.asList(1, 2, 39, 40, 41, 42, 43, 44, 45, 48, 49, 50),
				tool.getPageNumbers(42, 50));
		Assert.assertEquals(Arrays.asList(1, 2, 40, 41, 42, 43, 44, 45, 46, 48, 49, 50),
				tool.getPageNumbers(43, 50));
		Assert.assertEquals(Arrays.asList(1, 2, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50),
				tool.getPageNumbers(44, 50));
		Assert.assertEquals(Arrays.asList(1, 2, 42, 43, 44, 45, 46, 47, 48, 49, 50), tool.getPageNumbers(45, 50));
		Assert.assertEquals(Arrays.asList(1, 2, 43, 44, 45, 46, 47, 48, 49, 50), tool.getPageNumbers(46, 50));
		Assert.assertEquals(Arrays.asList(1, 2, 44, 45, 46, 47, 48, 49, 50), tool.getPageNumbers(47, 50));
		Assert.assertEquals(Arrays.asList(1, 2, 45, 46, 47, 48, 49, 50), tool.getPageNumbers(48, 50));
		Assert.assertEquals(Arrays.asList(1, 2, 46, 47, 48, 49, 50), tool.getPageNumbers(49, 50));
		Assert.assertEquals(Arrays.asList(1, 2, 47, 48, 49, 50), tool.getPageNumbers(50, 50));
		// CHECKSTYLE:ON
	}
}
