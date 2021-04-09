package org.demyo.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Album}.
 */
class AlbumTest {
	/**
	 * Tests number formatting in {@link Album#getIdentifyingName()}. Relates to commit
	 * 985b825e65dcf0949a4f6ccd5b0f39d2f528b11a.
	 */
	@Test
	void testNumberFormatting() {
		Album album = new Album();
		album.setTitle("Title");
		album.setNumber(BigDecimal.valueOf(1));
		assertThat(album.getIdentifyingName()).isEqualTo("1 - Title");

		album.setCycle(0);
		assertThat(album.getIdentifyingName()).isEqualTo("0.1 - Title");

		album.setNumber(BigDecimal.valueOf(1.2d));
		assertThat(album.getIdentifyingName()).isEqualTo("0.1.2 - Title");
	}
}
