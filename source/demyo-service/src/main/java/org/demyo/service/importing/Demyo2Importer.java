package org.demyo.service.importing;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

import jakarta.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.the4thlaw.commons.utils.io.IOUtils;
import org.the4thlaw.commons.utils.io.Sniffer;
import org.the4thlaw.commons.utils.xml.XMLUtils;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import org.demyo.common.config.SystemConfiguration;
import org.demyo.common.exception.DemyoErrorCode;
import org.demyo.common.exception.DemyoException;
import org.demyo.dao.IRawSQLDao;
import org.demyo.service.IImageService;
import org.demyo.service.IImportService;
import org.demyo.utils.io.ZipUtils;

/**
 * Importer for Demyo 2.x files.
 */
@Component
public class Demyo2Importer implements IImporter {
	private static final Logger LOGGER = LoggerFactory.getLogger(Demyo2Importer.class);

	private static final Pattern FORMAT_PATTERN = Pattern.compile(".*<library>.*<meta>.*<version.*", Pattern.DOTALL);

	@Autowired
	private IImportService importService;
	@Autowired
	private IImageService imageService;
	@Autowired
	protected IRawSQLDao rawSqlDao;

	@PostConstruct
	private void init() {
		importService.registerImporter(this);
	}

	@Override
	public boolean supports(String originalFilename, Path file) throws DemyoException {
		String originalFilenameLc = originalFilename.toLowerCase();

		if (originalFilenameLc.endsWith(".xml")) {
			return Sniffer.sniffFile(file, FORMAT_PATTERN);
		}

		return originalFilenameLc.endsWith(".dea");
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
			Path extractionDir = Files.createTempDirectory(SystemConfiguration.getInstance().getTempDirectory(),
				"extracted-import-");
			ZipUtils.extractZip(file, extractionDir);
			return extractionDir;
		} catch (IOException e) {
			throw new DemyoException(DemyoErrorCode.IMPORT_IO_ERROR, e);
		}
	}

	@Override
	public void importFile(String originalFilename, Path file) throws DemyoException {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		Path archiveDirectory = null;
		InputStream xmlFis = null;
		BufferedInputStream xmlBis = null;

		try {
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("Starting import, file size is {} bytes", Files.size(file));
			}

			// Extract if needed
			String originalFilenameLc = originalFilename.toLowerCase();
			boolean isArchive = originalFilenameLc.endsWith(".dea");

			Path xmlFile;
			if (isArchive) {
				archiveDirectory = extractZip(file);
				xmlFile = archiveDirectory.resolve("demyo.xml");
			} else {
				xmlFile = file;
			}

			stopWatch.split();
			long splitTime = stopWatch.getSplitTime();

			// Create a SAX parser for the input file
			XMLReader xmlReader = XMLUtils.createXmlReader();
			xmlFis = Files.newInputStream(xmlFile);
			xmlBis = new BufferedInputStream(xmlFis);

			// Import
			xmlReader.setContentHandler(new Demyo2Handler(rawSqlDao));
			xmlReader.parse(new InputSource(xmlBis));

			stopWatch.split();
			splitTime = stopWatch.getSplitTime() - splitTime;

			rawSqlDao.fixAutoIncrements();

			// Move extracted images to the right directory
			if (isArchive) {
				restoreImages(archiveDirectory, "images");
			}
			stopWatch.stop();

			LOGGER.info("Import took {}ms: {}ms in database and {}ms in I/O operations", stopWatch.getTime(), splitTime,
					stopWatch.getTime() - splitTime);
		} catch (IOException ioe) {
			throw new DemyoException(DemyoErrorCode.IMPORT_IO_ERROR, ioe);
		} catch (SAXException | ParserConfigurationException saxe) {
			if (saxe.getCause() instanceof DemyoException de) {
				// Rethrow exceptions of the right type
				throw de;
			}
			throw new DemyoException(DemyoErrorCode.IMPORT_PARSE_ERROR, saxe);
		} finally {
			IOUtils.closeQuietly(xmlBis);
			IOUtils.closeQuietly(xmlFis);
			org.the4thlaw.commons.utils.io.FileUtils.deleteDirectoryQuietly(archiveDirectory);
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

		imageService.clearCachedThumbnails();
	}
}
