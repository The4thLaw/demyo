package org.demyo.service.importing;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.demyo.dao.impl.IRawSQLDao;
import org.demyo.model.config.SystemConfiguration;
import org.demyo.model.exception.DemyoErrorCode;
import org.demyo.model.exception.DemyoException;
import org.demyo.service.IImportService;
import org.demyo.utils.io.DIOUtils;
import org.demyo.utils.io.ZipUtils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Importer for Demyo 2.x files.
 * 
 * @author $Author: xr $
 * @version $Revision: 1074 $
 */
@Component
public class Demyo2Importer implements IImporter {
	private static final Logger LOGGER = LoggerFactory.getLogger(Demyo2Importer.class);

	@Autowired
	private IImportService importService;
	@Autowired
	private IRawSQLDao rawSqlDao;

	@PostConstruct
	private void init() {
		importService.registerImporter(this);
	}

	@Override
	public boolean supports(String originalFilename, File file) throws DemyoException {
		String originalFilenameLc = originalFilename.toLowerCase();
		return originalFilenameLc.endsWith(".dea");
	}

	/**
	 * Extracts a ZIP file to a new temporary directory.
	 * 
	 * @param file The file to extract.
	 * @return The temporary directory where the file was extracted.
	 * @throws IOException in case of error during extraction.
	 */
	protected File extractZip(File file) throws IOException {
		File extractionDir = DIOUtils.createTempDirectory("extracted-import", null, SystemConfiguration
				.getInstance().getTempDirectory());
		ZipUtils.extractZip(file, extractionDir);
		return extractionDir;
	}

	@Override
	public void importFile(String originalFilename, File file) throws DemyoException {
		// TODO: never tested
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		File archiveDirectory = null;
		FileInputStream xmlFis = null;
		BufferedInputStream xmlBis = null;

		try {
			// Extract
			try {
				archiveDirectory = extractZip(file);
			} catch (IOException e) {
				throw new DemyoException(DemyoErrorCode.IMPORT_IO_ERROR, e);
			}
			File xmlFile = new File(archiveDirectory, "demyo.xml");

			stopWatch.split();
			long splitTime = stopWatch.getSplitTime();

			// Create a SAX parser for the input file
			SAXParserFactory spf = SAXParserFactory.newInstance();
			spf.setNamespaceAware(true);
			SAXParser saxParser = spf.newSAXParser();
			XMLReader xmlReader = saxParser.getXMLReader();
			xmlFis = new FileInputStream(xmlFile);
			xmlBis = new BufferedInputStream(xmlFis);

			// Import
			xmlReader.setContentHandler(new Demyo2Handler());
			xmlReader.parse(new InputSource(xmlBis));

			stopWatch.split();
			splitTime = stopWatch.getSplitTime() - splitTime;

			// Move extracted images to the right directory
			restoreImages(archiveDirectory, "images");
			stopWatch.stop();

			LOGGER.info("Import took {}ms: {}ms in database and {}ms in I/O operations", stopWatch.getTime(),
					splitTime, stopWatch.getTime() - splitTime);
		} catch (IOException ioe) {
			throw new DemyoException(DemyoErrorCode.IMPORT_IO_ERROR, ioe);
		} catch (SAXException | ParserConfigurationException saxe) {
			throw new DemyoException(DemyoErrorCode.IMPORT_PARSE_ERROR, saxe);
		} finally {
			DIOUtils.closeQuietly(xmlBis);
			DIOUtils.closeQuietly(xmlFis);
			DIOUtils.deleteDirectory(archiveDirectory);
		}
	}

	/**
	 * Restores the images from the imported archive in the local database.
	 * 
	 * @param archiveDirectory The path to the archive directory.
	 * @param imagesDirectoryName The name of the images directory in the archive.
	 * @throws DemyoException If moving the images fails.
	 */
	protected void restoreImages(File archiveDirectory, String imagesDirectoryName) throws DemyoException {
		File systemDirectory = SystemConfiguration.getInstance().getImagesDirectory();
		File extractedDirectory = new File(archiveDirectory, imagesDirectoryName);

		LOGGER.debug("Keeping backup copy of current images directory");
		File backupCopyDestination = new File(systemDirectory.getParentFile(), systemDirectory.getName() + ".bak");
		try {
			FileUtils.moveDirectory(systemDirectory, backupCopyDestination);
		} catch (IOException e) {
			throw new DemyoException(DemyoErrorCode.IMPORT_IMAGES_ERROR, e);
		}

		LOGGER.debug("Moving extracted directory from {} to {}", extractedDirectory, systemDirectory);
		try {
			FileUtils.moveDirectory(extractedDirectory, systemDirectory);
		} catch (IOException e) {
			throw new DemyoException(DemyoErrorCode.IMPORT_IMAGES_ERROR, e);
		}

		LOGGER.debug("Removing backup copy");
		DIOUtils.deleteDirectory(backupCopyDestination);
	}

	/**
	 * SAX handler for import of Demyo 2.x files.
	 * 
	 * @author $Author: xr $
	 * @version $Revision: 1074 $
	 */
	public class Demyo2Handler extends DefaultHandler {
		private String seriesId = null;
		private String albumId = null;
		private String derivativeId = null;

		private List<Map<String, String>> relatedSeries = new ArrayList<Map<String, String>>();
		private List<Map<String, String>> albumArtists = new ArrayList<Map<String, String>>();
		private List<Map<String, String>> albumWriters = new ArrayList<Map<String, String>>();
		private List<Map<String, String>> albumColorists = new ArrayList<Map<String, String>>();
		private List<Map<String, String>> albumTags = new ArrayList<Map<String, String>>();
		private List<Map<String, String>> albumImages = new ArrayList<Map<String, String>>();
		private List<Map<String, String>> derivativeImages = new ArrayList<Map<String, String>>();
		private Map<String, List<Map<String, String>>> allRelations = new HashMap<String, List<Map<String, String>>>();

		/**
		 * Initializes structures for the handler.
		 */
		public Demyo2Handler() {
			allRelations.put("series_relations", relatedSeries);
			allRelations.put("albums_artists", albumArtists);
			allRelations.put("albums_writers", albumWriters);
			allRelations.put("albums_colorists", albumColorists);
			allRelations.put("albums_tags", albumTags);
			allRelations.put("albums_images", albumImages);
			allRelations.put("derivatives_images", derivativeImages);
		}

		// TODO: manage meta version to warn if the schema version is different, except if it's Demyo 1.5 or earlier
		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes)
				throws SAXException {
			if ("image".equals(localName)) {
				createLine("images", attributes);
			} else if ("publisher".equals(localName)) {
				createLine("publishers", attributes);
			} else if ("collection".equals(localName)) {
				createLine("collections", attributes);
			} else if ("binding".equals(localName)) {
				createLine("bindings", attributes);
			} else if ("author".equals(localName)) {
				createLine("authors", attributes);
			} else if ("tag".equals(localName)) {
				createLine("tags", attributes);
			} else if ("series".equals(localName)) {
				seriesId = attributes.getValue("id");
				createLine("series", attributes);
			} else if ("related_series".equals(localName)) {
				HashMap<String, String> columns = new HashMap<String, String>();
				columns.put("main", seriesId);
				columns.put("sub", attributes.getValue("ref"));
				relatedSeries.add(columns);
			} else if ("album".equals(localName)) {
				albumId = attributes.getValue("id");
				createLine("albums", attributes);
			} else if ("artist".equals(localName)) {
				HashMap<String, String> columns = new HashMap<String, String>();
				columns.put("album_id", albumId);
				columns.put("artist_id", attributes.getValue("ref"));
				albumArtists.add(columns);
			} else if ("writer".equals(localName)) {
				HashMap<String, String> columns = new HashMap<String, String>();
				columns.put("album_id", albumId);
				columns.put("writer_id", attributes.getValue("ref"));
				albumWriters.add(columns);
			} else if ("colorist".equals(localName)) {
				HashMap<String, String> columns = new HashMap<String, String>();
				columns.put("album_id", albumId);
				columns.put("colorist_id", attributes.getValue("ref"));
				albumColorists.add(columns);
			} else if ("album-tag".equals(localName)) {
				HashMap<String, String> columns = new HashMap<String, String>();
				columns.put("album_id", albumId);
				columns.put("tag_id", attributes.getValue("ref"));
				albumTags.add(columns);
			} else if ("album-image".equals(localName)) {
				HashMap<String, String> columns = new HashMap<String, String>();
				columns.put("album_id", albumId);
				columns.put("image_id", attributes.getValue("ref"));
				albumImages.add(columns);
			} else if ("album_price".equals(localName)) {
				createLine("album_prices", attributes);
			} else if ("borrower".equals(localName)) {
				createLine("borrowers", attributes);
			} else if ("loan".equals(localName)) {
				createLine("albums_borrowers", attributes);
			} else if ("derivative_type".equals(localName)) {
				createLine("derivative_types", attributes);
			} else if ("source".equals(localName)) {
				createLine("sources", attributes);
			} else if ("derivative".equals(localName)) {
				derivativeId = attributes.getValue("id");
				createLine("derivatives", attributes);
			} else if ("derivative-image".equals(localName)) {
				HashMap<String, String> columns = new HashMap<String, String>();
				columns.put("derivative_id", derivativeId);
				columns.put("image_id", attributes.getValue("ref"));
				derivativeImages.add(columns);
			} else if ("derivative_price".equals(localName)) {
				createLine("derivative_prices", attributes);
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			if ("series".equals(localName)) {
				seriesId = null;
			} else if ("albums".equals(localName)) {
				albumId = null;
			} else if ("derivatives".equals(localName)) {
				derivativeId = null;
			} else if ("library".equals(localName)) {
				persistRelations();
			}
		}

		private void createLine(String tableName, Attributes attributes) {
			Map<String, String> attributeMap = new HashMap<String, String>(attributes.getLength());
			for (int i = 0; i < attributes.getLength(); i++) {
				attributeMap.put(attributes.getLocalName(i), attributes.getValue(i));
			}
			rawSqlDao.insert(tableName, attributeMap);
		}

		private void persistRelations() {
			LOGGER.debug("Persisting many-to-many relationships");
			for (Entry<String, List<Map<String, String>>> entry : allRelations.entrySet()) {
				String tableName = entry.getKey();
				List<Map<String, String>> tableContent = entry.getValue();
				LOGGER.debug("{} entries for {}", tableContent.size(), tableName);
				for (Map<String, String> line : tableContent) {
					rawSqlDao.insert(tableName, line);
				}
			}
		}
	}
}
