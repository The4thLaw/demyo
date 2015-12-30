package org.demyo.model;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link Image}.
 */
public class ImageTest {
	@Test
	public void testGetUserFileName() {
		Image i1 = new Image();
		i1.setDescription("My nice image");
		i1.setUrl("/path/to/stuff.png");
		Assert.assertEquals("My nice image.png", i1.getUserFileName());
	}
}
