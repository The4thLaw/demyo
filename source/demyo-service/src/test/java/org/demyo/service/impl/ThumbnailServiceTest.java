package org.demyo.service.impl;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.util.ReflectionTestUtils;
import org.the4thlaw.utils.image.ImageUtils;
import org.the4thlaw.utils.io.FileUtils;

import org.demyo.common.exception.DemyoException;
import org.demyo.service.IThumbnailService;
import org.demyo.service.ImageRetrievalResponse;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests from {@link ThumbnailService}.
 */
class ThumbnailServiceTest {
	static class FailingExecutor extends ThreadPoolExecutor {
		FailingExecutor() {
			super(1, 1, 1, TimeUnit.DAYS, new LinkedBlockingQueue<>(1));
		}

		@Override
		public <T> Future<T> submit(Callable<T> task) {
			throw new RejectedExecutionException("Always reject");
		}
	}

	private Path testDir;
	private Path thumbDir;
	private ThumbnailService service;

	/**
	 * Sets up the service and various directories.
	 *
	 * @throws IOException If creating the directories fails.
	 */
	@BeforeEach
	void setup() throws IOException {
		testDir = Files.createTempDirectory("ThumbnailServiceTest");
		thumbDir = testDir.resolve("thumbnails");
		service = new ThumbnailService(thumbDir, 1);
	}

	/**
	 * Cleans the directories.
	 */
	@AfterEach
	void tearDown() {
		FileUtils.deleteDirectoryQuietly(testDir);
	}

	private Path copyImage(String name) throws IOException, URISyntaxException {
		URL url = ThumbnailServiceTest.class.getResource("/" + name);
		assertThat(url).withFailMessage(() -> name + " not found").isNotNull();
		File source = Paths.get(url.toURI()).toFile();
		Path destination = testDir.resolve(name);
		Files.copy(source.toPath(), destination);
		return destination;
	}

	/**
	 * Tests
	 * {@link ThumbnailService#getThumbnail(long, int, boolean, org.demyo.service.impl.ThumbnailService.ImageSupplier)}
	 * .
	 *
	 * <p>
	 * This is the case for a generated thumbnail.
	 * </p>
	 *
	 * @param imageName The name of the image to use as source.
	 * @throws DemyoException If generation fails.
	 * @throws IOException If copying the source or asserting the destination fails.
	 * @throws URISyntaxException If getting the source fails.
	 */
	@ParameterizedTest
	@ValueSource(strings =
	{ "image-600x800.jpg", "image-600x800.png" })
	void getThumbnailGenerated(String imageName)
			throws DemyoException, IOException, URISyntaxException {
		Path imagePath = copyImage(imageName);

		// Generate and cache hit
		for (int i = 0; i < 2; i++) {
			File thumbFile = assertThumbnailGenerated(service, imagePath, 200);
			assertThat(thumbFile).isEqualTo(thumbDir.resolve("200w").resolve("42.jpg").toFile());
			assertThat(ImageUtils.getImageWidth(thumbFile)).isEqualTo(200);
		}
	}

	/**
	 * Tests
	 * {@link ThumbnailService#getThumbnail(long, int, boolean, org.demyo.service.impl.ThumbnailService.ImageSupplier)}
	 * .
	 *
	 * <p>
	 * This is the case for a an exact match to the original.
	 * </p>
	 *
	 * @param imageName The name of the image to use as source.
	 * @throws DemyoException If generation fails.
	 * @throws IOException If copying the source or asserting the destination fails.
	 * @throws URISyntaxException If getting the source fails.
	 */
	@ParameterizedTest
	@ValueSource(strings =
	{ "image-600x800.jpg", "image-600x800.png" })
	void getThumbnailOriginal(String imageName)
			throws DemyoException, IOException, URISyntaxException {
		Path imagePath = copyImage(imageName);
		File thumbFile = assertThumbnailGenerated(service, imagePath, 600);
		assertThat(thumbFile).isEqualTo(testDir.resolve(imageName).toFile());
		assertThat(thumbDir.toFile().listFiles()).isNull();
	}

	/**
	 * Tests
	 * {@link ThumbnailService#getThumbnail(long, int, boolean, org.demyo.service.impl.ThumbnailService.ImageSupplier)}
	 * .
	 *
	 * <p>
	 * This is the case for a lenient match to the original.
	 * </p>
	 *
	 * @param imageName The name of the image to use as source.
	 * @throws DemyoException If generation fails.
	 * @throws IOException If copying the source or asserting the destination fails.
	 * @throws URISyntaxException If getting the source fails.
	 */
	@ParameterizedTest
	@ValueSource(strings =
	{ "image-600x800.jpg", "image-600x800.png" })
	void getThumbnailLenient(String imageName)
			throws DemyoException, IOException, URISyntaxException {
		Path imagePath = copyImage(imageName);
		File thumbFile = assertThumbnailGenerated(service, imagePath, 620);
		assertThat(thumbFile).isEqualTo(testDir.resolve(imageName).toFile());
		assertThat(thumbDir.toFile().listFiles()).isNull();
	}

	/**
	 * Tests
	 * {@link ThumbnailService#getThumbnail(long, int, boolean, org.demyo.service.impl.ThumbnailService.ImageSupplier)}
	 * .
	 *
	 * <p>
	 * This is the case for the need for a fallback thumbnail due to a generation issue.
	 * </p>
	 *
	 * @param imageName The name of the image to use as source.
	 * @throws DemyoException If generation fails.
	 * @throws IOException If copying the source or asserting the destination fails.
	 * @throws URISyntaxException If getting the source fails.
	 */
	@ParameterizedTest
	@ValueSource(strings =
	{ "image-600x800.jpg", "image-600x800.png" })
	void getThumbnailFallback(String imageName)
			throws DemyoException, IOException, URISyntaxException {
		Path imagePath = copyImage(imageName);

		// Make some generations at diverse widths
		assertThumbnailGenerated(service, imagePath, 100);
		assertThumbnailGenerated(service, imagePath, 200);
		assertThumbnailGenerated(service, imagePath, 300);

		// Then create another service just for this case
		ThumbnailService serviceFail = new ThumbnailService(thumbDir, 1);
		// And set the executor to a service that will always fail
		ReflectionTestUtils.setField(serviceFail, "executor", new FailingExecutor());

		// Now call and assert
		ImageRetrievalResponse thumbResp = serviceFail.getThumbnail(42, 225, true, () -> imagePath);
		assertThat(thumbResp).isNotNull();
		assertThat(thumbResp.isExact()).isFalse();
		File thumbFile = thumbResp.getResource().getFile();
		assertThat(thumbFile)
				.exists()
				.isEqualTo(thumbDir.resolve("200w").resolve("42.jpg").toFile());
		assertThat(ImageUtils.getImageWidth(thumbFile)).isEqualTo(200);
	}

	private static File assertThumbnailGenerated(IThumbnailService service, Path imagePath, int width)
			throws DemyoException, IOException {
		ImageRetrievalResponse thumbResp = service.getThumbnail(42, width, true, () -> imagePath);
		assertThat(thumbResp).isNotNull();
		assertThat(thumbResp.isExact()).isTrue();
		File thumbFile = thumbResp.getResource().getFile();
		assertThat(thumbFile).exists();
		return thumbFile;
	}
}
