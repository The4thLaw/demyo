package org.demyo.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
			destinationFile.delete();
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
