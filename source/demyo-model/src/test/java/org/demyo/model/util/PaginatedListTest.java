package org.demyo.model.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link PaginatedList}.
 */
public class PaginatedListTest {
	private static final List<Void> EMPTY_LIST = Collections.<Void>emptyList();

	/** Tests {@link PaginatedList#getMaxPages()}. */
	@Test
	public void testMaxPages() {
		PaginatedList<Void> list;

		list = new PaginatedList<Void>(EMPTY_LIST, 1, 10, 9);
		assertThat(list.getMaxPages()).isEqualTo(1);

		list = new PaginatedList<Void>(EMPTY_LIST, 1, 10, 10);
		assertThat(list.getMaxPages()).isEqualTo(1);

		list = new PaginatedList<Void>(EMPTY_LIST, 1, 10, 11);
		assertThat(list.getMaxPages()).isEqualTo(2);
	}

	/** Tests {@link PaginatedList#hasPreviousPage()} and {@link PaginatedList#hasNextPage()}. */
	@Test
	public void testPreviousNext() {
		PaginatedList<Void> list;

		list = new PaginatedList<Void>(EMPTY_LIST, 1, 10, 95);
		assertThat(list.hasPreviousPage()).isFalse();
		assertThat(list.hasNextPage()).isTrue();

		list = new PaginatedList<Void>(EMPTY_LIST, 5, 10, 95);
		assertThat(list.hasPreviousPage()).isTrue();
		assertThat(list.hasNextPage()).isTrue();

		list = new PaginatedList<Void>(EMPTY_LIST, 10, 10, 95);
		assertThat(list.hasPreviousPage()).isTrue();
		assertThat(list.hasNextPage()).isFalse();
	}
}
