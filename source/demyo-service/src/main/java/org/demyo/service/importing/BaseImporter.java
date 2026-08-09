package org.demyo.service.importing;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.the4thlaw.commons.services.io.IDirectoryService;
import org.the4thlaw.commons.utils.io.ZipUtils;

import org.demyo.common.config.SystemConfiguration;
import org.demyo.common.exception.DemyoErrorCode;
import org.demyo.common.exception.DemyoException;

public abstract class BaseImporter implements IImporter {
	private static final Logger LOGGER = LoggerFactory.getLogger(BaseImporter.class);

	protected final IDirectoryService directoryService;

	public BaseImporter(IDirectoryService directoryService) {
		this.directoryService = directoryService;
	}

	/**
	 * Extracts a ZIP file to a new temporary directory.
	 *
	 * @param file The file to extract.
	 * @return The temporary directory where the file was extracted.
	 * @throws DemyoException in case of error during extraction.
	 */
	protected Path extractZip(Path file) throws DemyoException {
		try {
			Path extractionDir = directoryService.createTempDirectory("extracted-import-");
			ZipUtils.extractZip(file, extractionDir);
			return extractionDir;
		} catch (IOException e) {
			throw new DemyoException(DemyoErrorCode.IMPORT_IO_ERROR, e);
		}
	}

	/**
	 * Restores the images from the imported archive in the local database.
	 *
	 * @param archiveDirectory The path to the archive directory.
	 * @param imagesDirectoryName The name of the images directory in the archive.
	 * @throws DemyoException If moving the images fails.
	 */
	protected void restoreImages(Path archiveDirectory, String imagesDirectoryName) throws DemyoException {
		File systemDirectory = SystemConfiguration.getInstance().getImagesDirectory().toFile();
		File extractedDirectory = archiveDirectory.resolve(imagesDirectoryName).toFile();

		LOGGER.debug("Keeping backup copy of current images directory");
		File backupCopyDestination = new File(systemDirectory.getParentFile(), systemDirectory.getName() + ".bak");
		try {
			FileUtils.moveDirectory(systemDirectory, backupCopyDestination);
		} catch (IOException e) {
			throw new DemyoException(DemyoErrorCode.IMPORT_IMAGES_ERROR, e);
		}

		LOGGER.debug("Moving extracted directory from {} to {}", extractedDirectory, systemDirectory);
		try {
			// We need commons-io here because it can manage moving between file stores, which could happen
			// for the extraction directory. It also requires us to work with java.io.File
			FileUtils.moveDirectory(extractedDirectory, systemDirectory);
		} catch (IOException e) {
			throw new DemyoException(DemyoErrorCode.IMPORT_IMAGES_ERROR, e);
		}

		LOGGER.debug("Removing backup copy");
		org.the4thlaw.commons.utils.io.FileUtils.deleteDirectoryQuietly(backupCopyDestination);

		directoryService.clearThumbnails();
	}
}
