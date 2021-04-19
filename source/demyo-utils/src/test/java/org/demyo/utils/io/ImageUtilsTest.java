package org.demyo.utils.io;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import org.demyo.common.exception.DemyoException;

/**
 * Tests for {@link ImageUtils}.
 */
class ImageUtilsTest {
	private static File getFile(String resourcePath) throws URISyntaxException {
		URL url = ImageUtilsTest.class.getResource("/image-42x16.png");
		assertThat(url).withFailMessage(() -> resourcePath + " not found").isNotNull();
		return Paths.get(url.toURI()).toFile();
	}

	/**
	 * Tests {@link ImageUtils#getImageWidth(File)}.
	 * 
	 * @throws DemyoException In case of error while getting the image width.
	 * @throws URISyntaxException In case of error while getting the test data.
	 */
	@Test
	void getImageWidth() throws DemyoException, URISyntaxException {
		assertThat(ImageUtils.getImageWidth(getFile("/image-42x16.jpg"))).isEqualTo(42);
		assertThat(ImageUtils.getImageWidth(getFile("/image-42x16.png"))).isEqualTo(42);
	}
}
