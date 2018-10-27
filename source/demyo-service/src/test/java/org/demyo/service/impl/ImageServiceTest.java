package org.demyo.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;

import org.demyo.common.config.SystemConfiguration;
import org.demyo.common.exception.DemyoException;
import org.demyo.dao.IImageRepo;
import org.demyo.model.Image;
import org.demyo.service.IFilePondService;

/**
 * Unit tests for {@link ImageService}.
 */
public class ImageServiceTest extends AbstractServiceTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(ImageServiceTest.class);

	private static File generateDummyImmage() {
		String fileName = "target/ImageServiceTest_" + UUID.randomUUID().toString() + ".jpg";
		File file = new File(fileName);
		try (FileOutputStream fos = new FileOutputStream(file)) {
			fos.write(fileName.getBytes());
		} catch (IOException e) {
			throw new RuntimeException("generateDummyImage failed", e);
		}
		return file;
	}

	private static void cleanDummyImage(Image image) {
		File f = new File(SystemConfiguration.getInstance().getImagesDirectory(), image.getUrl());
		if (!f.delete()) {
			LOGGER.debug("Failed to delete dummy image: {}", f);
		}
	}

	private static ImageService createImageServiceForRecovery() throws DemyoException {
		ImageService service = new ImageService();
		IImageRepo repo = Mockito.mock(IImageRepo.class);
		IFilePondService fpService = Mockito.mock(FilePondService.class);

		ReflectionTestUtils.setField(service, "repo", repo);
		ReflectionTestUtils.setField(service, "filePondService", fpService);

		// TODO: Java 8: use a lambda for this?
		when(fpService.getFileForId(Matchers.anyString())).thenAnswer(new Answer<File>() {
			// We must generate a new File for each invocation
			@Override
			public File answer(InvocationOnMock invocation) throws Throwable {
				return generateDummyImmage();
			}
		});
		when(repo.save(Matchers.<Image>any())).thenAnswer(new Answer<Image>() {
			@Override
			public Image answer(InvocationOnMock invocation) throws Throwable {
				return invocation.getArgumentAt(0, Image.class);
			}
		});
		return service;
	}

	/**
	 * Tests that recovering a single image where no other image already exists works.
	 * 
	 * @throws DemyoException In case of error.
	 */
	@Test
	public void testRecoverSingleImageNoneExisting() throws DemyoException {
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
	public void testRecoverSingleImageButExists() throws DemyoException {
		ImageService service = createImageServiceForRecovery();

		IImageRepo repo = (IImageRepo) service.getRepo();

		Image existing1 = new Image();
		existing1.setDescription("Album Name 01 - Cover");

		when(repo.findByDescriptionLike(Matchers.anyString())).thenReturn(Collections.singletonList(existing1));

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
	public void testRecoverMultipleImageNoneExisting() throws DemyoException {
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
	public void testRecoverMultipleImageButExists() throws DemyoException {
		ImageService service = createImageServiceForRecovery();

		IImageRepo repo = (IImageRepo) service.getRepo();

		Image existing1 = new Image();
		existing1.setDescription("Album Name 01 - Image 1");
		Image existing2 = new Image();
		existing2.setDescription("Album Name 01 - Image 2");

		when(repo.findByDescriptionLike(Matchers.anyString())).thenReturn(Arrays.asList(existing1, existing2));

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
