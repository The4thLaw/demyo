package org.demyo.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

	private final File uploadDirectory;

	/**
	 * Default constructor.
	 */
	public FilePondService() {
		uploadDirectory = new File(SystemConfiguration.getInstance().getTempDirectory(), UPLOAD_DIRECTORY_NAME);
		uploadDirectory.mkdirs();
	}

	/**
	 * Cleans the FilePond upload directory. Removes files older than {@link #AUTOCLEAN_MIN_AGE}.
	 */
	@Scheduled(initialDelay = AUTOCLEAN_DELAY, fixedRate = AUTOCLEAN_PERIOD)
	public void cleanFilePondDirectory() {
		LOGGER.debug("Auto-cleaning FilePond directory...");
		Collection<File> filesToDelete = FileUtils.listFiles(uploadDirectory, new AbstractFileFilter() {
			@Override
			public boolean accept(File file) {
				return System.currentTimeMillis() - file.lastModified() > AUTOCLEAN_MIN_AGE;
			}

		}, TrueFileFilter.INSTANCE);

		for (File f : filesToDelete) {
			LOGGER.debug("Auto-cleaning FilePond file: {}", f);
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
		File destinationFile = new File(uploadDirectory, id + "." + extension);

		try (FileOutputStream fos = new FileOutputStream(destinationFile)) {
			IOUtils.copy(input, fos);
		} catch (IOException ioe) {
			DIOUtils.delete(destinationFile);
		}
		// Request to delete on exit, just in case
		destinationFile.deleteOnExit();

		LOGGER.debug("{} was stored to {}", originalFileName, destinationFile.getAbsolutePath());

		return destinationFile.getName();
	}

	@Override
	public void revert(String id) {
		File file = new File(uploadDirectory, id);

		if (!file.exists()) {
			LOGGER.debug("{} doesn't exist already, not doing anything", file.getAbsolutePath());
			return;
		}

		if (!file.delete()) {
			LOGGER.debug("Failed to delete {}", file.getAbsolutePath());
			return;
		}

		LOGGER.debug("Successfully deleted {}", file.getAbsolutePath());
	}

	@Override
	public File getFileForId(String id) throws DemyoException {
		File file = new File(uploadDirectory, id);

		if (!file.exists()) {
			LOGGER.warn("{} doesn't exist", file.getAbsolutePath());
			throw new DemyoException(DemyoErrorCode.IMAGE_FILEPOND_MISSING, id + " doesn't exist");
		}

		return file;
	}

}
