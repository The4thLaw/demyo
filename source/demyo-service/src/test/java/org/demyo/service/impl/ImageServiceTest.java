package org.demyo.service.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import org.the4thlaw.commons.utils.io.FileUtils;

import org.demyo.common.config.SystemConfiguration;
import org.demyo.common.exception.DemyoException;
import org.demyo.dao.IImageRepo;
import org.demyo.model.Image;
import org.demyo.service.IFilePondService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ImageService}.
 */
class ImageServiceTest extends AbstractServiceTest {
	private static Path generateDummyImmage() {
		String fileName = "target/ImageServiceTest_" + UUID.randomUUID().toString() + ".jpg";
		Path file = Path.of(fileName);
		try (OutputStream os = Files.newOutputStream(file)) {
			os.write(fileName.getBytes());
		} catch (IOException e) {
			throw new RuntimeException("generateDummyImage failed", e);
		}
		return file;
	}

	private static void cleanDummyImage(Image image) {
		Path f = SystemConfiguration.getInstance().getImagesDirectory().resolve(image.getUrl());
		FileUtils.deleteQuietly(f);
	}

	private static ImageService createImageServiceForRecovery() throws DemyoException {
		ImageService service = new ImageService();
		IImageRepo repo = Mockito.mock(IImageRepo.class);
		IFilePondService fpService = Mockito.mock(FilePondService.class);

		ReflectionTestUtils.setField(service, "repo", repo);
		ReflectionTestUtils.setField(service, "filePondService", fpService);

		when(fpService.getFileForId(ArgumentMatchers.anyString())).thenAnswer(invocation -> generateDummyImmage());
		when(repo.save(ArgumentMatchers.<Image>any())).thenAnswer(invocation -> invocation.getArgument(0, Image.class));
		return service;
	}

	/**
	 * Tests that recovering a single image where no other image already exists works.
	 *
	 * @throws DemyoException In case of error.
	 */
	@Test
	void testRecoverSingleImageNoneExisting() throws DemyoException {
		ImageService service = createImageServiceForRecovery();

		List<Image> saved = service.recoverImagesFromFilePond("Album Name 01 - Cover", false, "filePondFile1");
		assertThat(saved).hasSize(1);
		Image image = saved.get(0);
		assertThat(image.getDescription()).isEqualTo("Album Name 01 - Cover");

		cleanDummyImage(image);
	}

	/**
	 * Tests that recovering a single image where an image already exists with that name works.
	 *
	 * @throws DemyoException In case of error.
	 */
	@Test
	void testRecoverSingleImageButExists() throws DemyoException {
		ImageService service = createImageServiceForRecovery();

		IImageRepo repo = (IImageRepo) service.getRepo();

		Image existing1 = new Image();
		existing1.setDescription("Album Name 01 - Cover");

		when(repo.findByDescriptionLike(ArgumentMatchers.anyString())).thenReturn(Collections.singletonList(existing1));

		List<Image> saved = service.recoverImagesFromFilePond("Album Name 01 - Cover", false, "filePondFile1");
		assertThat(saved).hasSize(1);
		Image image = saved.get(0);
		assertThat(image.getDescription()).isEqualTo("Album Name 01 - Cover 1");

		cleanDummyImage(image);
	}

	/**
	 * Tests that recovering multiple images where image already exist with that name works.
	 *
	 * @throws DemyoException In case of error.
	 */
	@Test
	void testRecoverMultipleImageNoneExisting() throws DemyoException {
		ImageService service = createImageServiceForRecovery();

		List<Image> saved = service.recoverImagesFromFilePond("Album Name 01 - Image", true, "filePondFile1",
				"filePondFile2");
		assertThat(saved).hasSize(2);
		Image image1 = saved.get(0);
		assertThat(image1.getDescription()).isEqualTo("Album Name 01 - Image 1");
		Image image2 = saved.get(1);
		assertThat(image2.getDescription()).isEqualTo("Album Name 01 - Image 2");

		cleanDummyImage(image1);
		cleanDummyImage(image2);
	}

	/**
	 * Tests that recovering multiple images where image already exist with that name works.
	 *
	 * @throws DemyoException In case of error.
	 */
	@Test
	void testRecoverMultipleImageButExists() throws DemyoException {
		ImageService service = createImageServiceForRecovery();

		IImageRepo repo = (IImageRepo) service.getRepo();

		Image existing1 = new Image();
		existing1.setDescription("Album Name 01 - Image 1");
		Image existing2 = new Image();
		existing2.setDescription("Album Name 01 - Image 2");

		when(repo.findByDescriptionLike(ArgumentMatchers.anyString())).thenReturn(List.of(existing1, existing2));

		List<Image> saved = service.recoverImagesFromFilePond("Album Name 01 - Image", true, "filePondFile1",
				"filePondFile2");
		assertThat(saved).hasSize(2);
		Image image1 = saved.get(0);
		assertThat(image1.getDescription()).isEqualTo("Album Name 01 - Image 3");
		Image image2 = saved.get(1);
		assertThat(image2.getDescription()).isEqualTo("Album Name 01 - Image 4");

		cleanDummyImage(image1);
		cleanDummyImage(image2);
	}
}
