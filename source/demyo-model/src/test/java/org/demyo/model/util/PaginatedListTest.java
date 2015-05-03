package org.demyo.model.util;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link PaginatedList}.
 * 
 * @author $Author: xr $
 * @version $Revision: 1060 $
 */
public class PaginatedListTest {
	private static final List<Void> EMPTY_LIST = Collections.<Void> emptyList();

	/** Tests {@link PaginatedList#getMaxPages()}. */
	@Test
	public void testMaxPages() {
		PaginatedList<Void> list;

		list = new PaginatedList<Void>(EMPTY_LIST, 1, 10, 9);
		Assert.assertEquals(1, list.getMaxPages());

		list = new PaginatedList<Void>(EMPTY_LIST, 1, 10, 10);
		Assert.assertEquals(1, list.getMaxPages());

		list = new PaginatedList<Void>(EMPTY_LIST, 1, 10, 11);
		Assert.assertEquals(2, list.getMaxPages());
	}

	/** Tests {@link PaginatedList#hasPreviousPage()} and {@link PaginatedList#hasNextPage()}. */
	@Test
	public void testPreviousNext() {
		PaginatedList<Void> list;

		list = new PaginatedList<Void>(EMPTY_LIST, 1, 10, 95);
		Assert.assertFalse(list.hasPreviousPage());
		Assert.assertTrue(list.hasNextPage());

		list = new PaginatedList<Void>(EMPTY_LIST, 5, 10, 95);
		Assert.assertTrue(list.hasPreviousPage());
		Assert.assertTrue(list.hasNextPage());

		list = new PaginatedList<Void>(EMPTY_LIST, 10, 10, 95);
		Assert.assertTrue(list.hasPreviousPage());
		Assert.assertFalse(list.hasNextPage());
	}
}
