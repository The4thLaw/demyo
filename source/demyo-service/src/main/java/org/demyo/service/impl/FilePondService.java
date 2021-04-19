package org.demyo.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.AbstractFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import org.demyo.common.config.SystemConfiguration;
import org.demyo.common.exception.DemyoErrorCode;
import org.demyo.common.exception.DemyoException;
import org.demyo.common.exception.DemyoRuntimeException;
import org.demyo.service.IFilePondService;
import org.demyo.utils.io.DIOUtils;

/**
 * Implements the contract defined by {@link IFilePondService}.
 */
@Service
public class FilePondService implements IFilePondService {

	private static final long AUTOCLEAN_DELAY = 1000L * 60;
	private static final long AUTOCLEAN_PERIOD = 1000L * 60 * 60 * 24;
	private static final long AUTOCLEAN_MIN_AGE = 1000L * 60 * 60;
	private static final Logger LOGGER = LoggerFactory.getLogger(FilePondService.class);
	private static final String UPLOAD_DIRECTORY_NAME = "filepond";

	private final Path uploadDirectory;

	/**
	 * Default constructor.
	 */
	public FilePondService() {
		this(SystemConfiguration.getInstance().getTempDirectory().resolve(UPLOAD_DIRECTORY_NAME));
	}

	/**
	 * Constructor allowing to set the upload directory.
	 * 
	 * @param uploadDirectory The temporary directory for FilePond uploads.
	 */
	public FilePondService(Path uploadDirectory) {
		this.uploadDirectory = uploadDirectory;
		try {
			Files.createDirectories(uploadDirectory);
		} catch (IOException e) {
			throw new DemyoRuntimeException(DemyoErrorCode.SYS_IO_ERROR, e);
		}
	}

	/**
	 * Cleans the FilePond upload directory. Removes files older than {@link #AUTOCLEAN_MIN_AGE}.
	 */
	@Scheduled(initialDelay = AUTOCLEAN_DELAY, fixedRate = AUTOCLEAN_PERIOD)
	public void cleanFilePondDirectory() {
		LOGGER.debug("Auto-cleaning FilePond directory...");
		Collection<File> filesToDelete = FileUtils.listFiles(uploadDirectory.toFile(), new AbstractFileFilter() {
			@Override
			public boolean accept(File file) {
				return System.currentTimeMillis() - file.lastModified() > AUTOCLEAN_MIN_AGE;
			}

		}, TrueFileFilter.INSTANCE);

		for (File f : filesToDelete) {
			LOGGER.debug("Auto-cleaning FilePond file: {}", f);
			DIOUtils.delete(f);
		}
	}

	@Override
	public String process(String originalFileName, InputStream input) throws IOException {
		LOGGER.debug("Uploading a file through FilePond: {}", originalFileName);

		String extension = DIOUtils.getFileExtension(originalFileName);
		if (extension == null) {
			extension = "jpg";
		}

		String id = UUID.randomUUID().toString();

		String filename = id + "." + extension;
		Path destinationFile = uploadDirectory.resolve(filename);

		try (OutputStream fos = Files.newOutputStream(destinationFile)) {
			IOUtils.copy(input, fos);
		} catch (IOException ioe) {
			LOGGER.warn("Failed to store FilePond data to {}", destinationFile, ioe);
			DIOUtils.delete(destinationFile);
		}
		// Request to delete on exit, just in case
		destinationFile.toFile().deleteOnExit();

		LOGGER.debug("{} was stored to {}", originalFileName, destinationFile.toAbsolutePath());

		return filename;
	}

	@Override
	public void revert(String id) {
		Path file = uploadDirectory.resolve(id);
		DIOUtils.assertChildOf(uploadDirectory, file);
		DIOUtils.delete(file);
	}

	@Override
	public File getFileForId(String id) throws DemyoException {
		Path file = uploadDirectory.resolve(id);

		DIOUtils.assertChildOf(uploadDirectory, file);

		if (!(Files.exists(file) && Files.isRegularFile(file))) {
			LOGGER.warn("{} doesn't exist or isn't a regular file", file.toAbsolutePath());
			throw new DemyoException(DemyoErrorCode.IMAGE_FILEPOND_MISSING, id + " doesn't exist or isn't a file");
		}

		return file.toFile();
	}

}
