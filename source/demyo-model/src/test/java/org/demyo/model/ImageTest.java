package org.demyo.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Image}.
 */
public class ImageTest {
	@Test
	void testGetUserFileName() {
		Image i1 = new Image();
		i1.setDescription("My nice image");
		i1.setUrl("/path/to/stuff.png");
		assertThat(i1.getUserFileName()).isEqualTo("My nice image.png");
	}
}
