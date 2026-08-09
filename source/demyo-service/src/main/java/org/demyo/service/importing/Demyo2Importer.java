package org.demyo.service.importing;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;

import jakarta.annotation.PostConstruct;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.the4thlaw.commons.services.io.IDirectoryService;
import org.the4thlaw.commons.utils.io.IOUtils;
import org.the4thlaw.commons.utils.io.Sniffer;
import org.the4thlaw.commons.utils.xml.XMLUtils;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import org.demyo.common.exception.DemyoErrorCode;
import org.demyo.common.exception.DemyoException;
import org.demyo.dao.IRawSQLDao;
import org.demyo.service.IImportService;

/**
 * Importer for Demyo 2.x files.
 */
@Component
public class Demyo2Importer extends BaseImporter {
	private static final Logger LOGGER = LoggerFactory.getLogger(Demyo2Importer.class);

	private static final Pattern FORMAT_PATTERN = Pattern.compile(".*<library>.*<meta>.*<version.*", Pattern.DOTALL);

	@Autowired
	private IImportService importService;
	@Autowired
	protected IRawSQLDao rawSqlDao;
	@Autowired
	protected DataSource dataSource;

	public Demyo2Importer(IDirectoryService directoryService) {
		super(directoryService);
	}

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
			long splitTime = stopWatch.getSplitDuration().toMillis();

			// Create a SAX parser for the input file
			XMLReader xmlReader = XMLUtils.createXmlReader();
			xmlFis = Files.newInputStream(xmlFile);
			xmlBis = new BufferedInputStream(xmlFis);

			// Import
			xmlReader.setContentHandler(new Demyo2Handler(rawSqlDao, dataSource));
			xmlReader.parse(new InputSource(xmlBis));

			stopWatch.split();
			splitTime = stopWatch.getSplitDuration().toMillis() - splitTime;

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
}
