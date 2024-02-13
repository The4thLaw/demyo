package org.demyo.service.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.demyo.common.exception.DemyoException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link FilePondService}.
 */
class FilePondServiceTest {
	private static final String SAMPLE_FILE_CONTENT = "Foo";
	private static final String FILENAME_TO_KEEP = "toKeep.txt";
	private static final String FILENAME_TO_DELETE = "toDelete.txt";
	private static final String FILENAME_SAMPLE_DIR = "SampleDirectory";
	private static final long TSTAMP_2020 = 1587283246000L;

	private Path uploadDir;
	private FilePondService service;

	/**
	 * Sets up the service and temp directory.
	 *
	 * @throws IOException If creating the temp directory failed.
	 */
	@BeforeEach
	void setup() throws IOException {
		uploadDir = Files.createTempDirectory("FilePondServiceTest");
		service = new FilePondService(uploadDir);
	}

	/**
	 * Removes the temp directory.
	 */
	@AfterEach
	void tearDown() {
		org.the4thlaw.utils.io.FileUtils.deleteDirectoryQuietly(uploadDir);;
	}

	private static void createSampleFile(File toDelete) throws IOException {
		FileUtils.write(toDelete, SAMPLE_FILE_CONTENT, StandardCharsets.UTF_8);
	}

	/**
	 * Tests {@link FilePondService#cleanFilePondDirectory()}
	 *
	 * @throws IOException If creating the test data fails.
	 */
	@Test
	void cleanFilePondDirectory() throws IOException {
		File toDelete = uploadDir.resolve(FILENAME_TO_DELETE).toFile();
		createSampleFile(toDelete);
		toDelete.setLastModified(TSTAMP_2020);
		File toKeep = uploadDir.resolve(FILENAME_TO_KEEP).toFile();
		createSampleFile(toKeep);

		assertThat(toDelete).exists();
		assertThat(toKeep).exists();

		service.cleanFilePondDirectory();

		assertThat(toDelete).doesNotExist();
		assertThat(toKeep).exists();
	}

	/**
	 * Tests {@link FilePondService#process(String, java.io.InputStream)}
	 *
	 * @throws IOException If the processing fails.
	 */
	@Test
	void process() throws IOException {
		String id = service.process(FILENAME_TO_KEEP,
				new ByteArrayInputStream(SAMPLE_FILE_CONTENT.getBytes(StandardCharsets.UTF_8)));

		assertThat(id).endsWith(".txt");

		Path resolved = uploadDir.resolve(id);
		assertThat(resolved).exists();
	}

	/**
	 * Tests {@link FilePondService#revert(String)}.
	 *
	 * @throws IOException If creating the test data fails.
	 */
	@Test
	void revert() throws IOException {
		File toDelete = uploadDir.resolve(FILENAME_TO_DELETE).toFile();
		createSampleFile(toDelete);

		assertThat(toDelete).exists();

		service.revert(FILENAME_TO_DELETE);

		assertThat(toDelete).doesNotExist();
	}

	/**
	 * Tests {@link FilePondService#getFileForId(String)}.
	 *
	 * @throws IOException If creating the test data fails.
	 * @throws DemyoException If getting the valid file fails.
	 */
	@Test
	void getFileForId() throws IOException, DemyoException {
		File toKeep = uploadDir.resolve(FILENAME_TO_KEEP).toFile();
		createSampleFile(toKeep);
		Files.createDirectories(uploadDir.resolve(FILENAME_SAMPLE_DIR));

		assertThat(service.getFileForId(FILENAME_TO_KEEP)).isEqualTo(toKeep);
		assertThatThrownBy(() -> service.getFileForId(FILENAME_TO_DELETE)).isInstanceOf(DemyoException.class)
				.hasMessage("DEMYO-ERR-12004: IMAGE_FILEPOND_MISSING [toDelete.txt doesn't exist or isn't a file]");
		assertThatThrownBy(() -> service.getFileForId(FILENAME_SAMPLE_DIR)).isInstanceOf(DemyoException.class)
				.hasMessage("DEMYO-ERR-12004: IMAGE_FILEPOND_MISSING [SampleDirectory doesn't exist or isn't a file]");
	}
}
