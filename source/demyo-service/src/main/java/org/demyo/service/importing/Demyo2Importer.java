package org.demyo.service.importing;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.xml.parsers.ParserConfigurationException;

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

import org.demyo.common.config.SystemConfiguration;
import org.demyo.common.exception.DemyoErrorCode;
import org.demyo.common.exception.DemyoException;
import org.demyo.dao.IRawSQLDao;
import org.demyo.service.IImageService;
import org.demyo.service.IImportService;
import org.demyo.utils.io.DIOUtils;
import org.demyo.utils.io.ZipUtils;
import org.demyo.utils.xml.XMLUtils;

/**
 * Importer for Demyo 2.x files.
 */
@Component
public class Demyo2Importer implements IImporter {
	private static final Logger LOGGER = LoggerFactory.getLogger(Demyo2Importer.class);

	@Autowired
	private IImportService importService;
	@Autowired
	private IImageService imageService;
	@Autowired
	private IRawSQLDao rawSqlDao;

	@PostConstruct
	private void init() {
		importService.registerImporter(this);
	}

	@Override
	public boolean supports(String originalFilename, Path file) throws DemyoException {
		String originalFilenameLc = originalFilename.toLowerCase();

		if (originalFilenameLc.endsWith(".xml")) {
			return DIOUtils.sniffFile(file, Pattern.compile(".*<library>.*<meta>.*<version.*", Pattern.DOTALL));
		}

		return originalFilenameLc.endsWith(".dea");
	}

	/**
	 * Extracts a ZIP file to a new temporary directory.
	 * 
	 * @param file The file to extract.
	 * @return The temporary directory where the file was extracted.
	 * @throws IOException in case of error during extraction.
	 */
	protected Path extractZip(Path file) throws IOException {
		Path extractionDir = Files.createTempDirectory(SystemConfiguration.getInstance().getTempDirectory(),
				"extracted-import-");
		ZipUtils.extractZip(file, extractionDir);
		return extractionDir;
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
				try {
					archiveDirectory = extractZip(file);
				} catch (IOException e) {
					throw new DemyoException(DemyoErrorCode.IMPORT_IO_ERROR, e);
				}
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
			xmlReader.setContentHandler(new Demyo2Handler());
			xmlReader.parse(new InputSource(xmlBis));

			stopWatch.split();
			splitTime = stopWatch.getSplitTime() - splitTime;

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
			if (saxe.getCause() instanceof DemyoException) {
				// Rethrow exceptions of the right type
				throw (DemyoException) saxe.getCause();
			}
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
	protected void restoreImages(Path archiveDirectory, String imagesDirectoryName) throws DemyoException {
		File systemDirectory = SystemConfiguration.getInstance().getImagesDirectory();
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
			FileUtils.moveDirectory(extractedDirectory, systemDirectory);
		} catch (IOException e) {
			throw new DemyoException(DemyoErrorCode.IMPORT_IMAGES_ERROR, e);
		}

		LOGGER.debug("Removing backup copy");
		DIOUtils.deleteDirectory(backupCopyDestination);

		imageService.clearCachedThumbnails();
	}

	/**
	 * SAX handler for import of Demyo 2.x files.
	 */
	public class Demyo2Handler extends DefaultHandler {
		private static final String FK_ALBUM_ID = "album_id";
		private static final String FK_READER_ID = "reader_id";
		private String seriesId;
		private String albumId;
		private String derivativeId;
		private String readerId;

		private List<Map<String, String>> relatedSeries = new ArrayList<>();
		private List<Map<String, String>> albumArtists = new ArrayList<>();
		private List<Map<String, String>> albumWriters = new ArrayList<>();
		private List<Map<String, String>> albumColorists = new ArrayList<>();
		private List<Map<String, String>> albumInkers = new ArrayList<>();
		private List<Map<String, String>> albumTranslators = new ArrayList<>();
		private List<Map<String, String>> albumTags = new ArrayList<>();
		private List<Map<String, String>> albumImages = new ArrayList<>();
		private List<Map<String, String>> derivativeImages = new ArrayList<>();
		private List<Map<String, String>> readerFavouriteSeries = new ArrayList<>();
		private List<Map<String, String>> readerFavouriteAlbums = new ArrayList<>();
		private List<Map<String, String>> readerReadingList = new ArrayList<>();
		private Map<String, List<Map<String, String>>> allRelations = new HashMap<>();

		/**
		 * Initializes structures for the handler.
		 */
		public Demyo2Handler() {
			allRelations.put("series_relations", relatedSeries);
			allRelations.put("albums_artists", albumArtists);
			allRelations.put("albums_writers", albumWriters);
			allRelations.put("albums_colorists", albumColorists);
			allRelations.put("albums_inkers", albumInkers);
			allRelations.put("albums_translators", albumTranslators);
			allRelations.put("albums_tags", albumTags);
			allRelations.put("albums_images", albumImages);
			allRelations.put("derivatives_images", derivativeImages);
			allRelations.put("readers_favourite_series", readerFavouriteSeries);
			allRelations.put("readers_favourite_albums", readerFavouriteAlbums);
			allRelations.put("readers_reading_list", readerReadingList);
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes)
				throws SAXException {
			if ("version".equals(localName)) {
				handleVersion(attributes);
			} else if ("image".equals(localName)) {
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
				HashMap<String, String> columns = new HashMap<>();
				columns.put("main", seriesId);
				columns.put("sub", attributes.getValue("ref"));
				relatedSeries.add(columns);
			} else if ("album".equals(localName)) {
				albumId = attributes.getValue("id");
				createLine("albums", attributes);
			} else if ("artist".equals(localName)) {
				HashMap<String, String> columns = new HashMap<>();
				columns.put(FK_ALBUM_ID, albumId);
				columns.put("artist_id", attributes.getValue("ref"));
				albumArtists.add(columns);
			} else if ("writer".equals(localName)) {
				HashMap<String, String> columns = new HashMap<>();
				columns.put(FK_ALBUM_ID, albumId);
				columns.put("writer_id", attributes.getValue("ref"));
				albumWriters.add(columns);
			} else if ("colorist".equals(localName)) {
				HashMap<String, String> columns = new HashMap<>();
				columns.put(FK_ALBUM_ID, albumId);
				columns.put("colorist_id", attributes.getValue("ref"));
				albumColorists.add(columns);
			} else if ("inker".equals(localName)) {
				HashMap<String, String> columns = new HashMap<>();
				columns.put(FK_ALBUM_ID, albumId);
				columns.put("inker_id", attributes.getValue("ref"));
				albumInkers.add(columns);
			} else if ("translator".equals(localName)) {
				HashMap<String, String> columns = new HashMap<>();
				columns.put(FK_ALBUM_ID, albumId);
				columns.put("translator_id", attributes.getValue("ref"));
				albumTranslators.add(columns);
			} else if ("album-tag".equals(localName)) {
				HashMap<String, String> columns = new HashMap<>();
				columns.put(FK_ALBUM_ID, albumId);
				columns.put("tag_id", attributes.getValue("ref"));
				albumTags.add(columns);
			} else if ("album-image".equals(localName)) {
				HashMap<String, String> columns = new HashMap<>();
				columns.put(FK_ALBUM_ID, albumId);
				columns.put("image_id", attributes.getValue("ref"));
				albumImages.add(columns);
			} else if ("album_price".equals(localName)) {
				createLine("albums_prices", attributes);
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
				HashMap<String, String> columns = new HashMap<>();
				columns.put("derivative_id", derivativeId);
				columns.put("image_id", attributes.getValue("ref"));
				derivativeImages.add(columns);
			} else if ("derivative_price".equals(localName)) {
				createLine("derivatives_prices", attributes);
			} else if ("reader".equals(localName)) {
				readerId = attributes.getValue("id");
				createLine("readers", attributes);
			} else if ("favourite-series".equals(localName)) {
				HashMap<String, String> columns = new HashMap<>();
				columns.put(FK_READER_ID, readerId);
				columns.put("series_id", attributes.getValue("ref"));
				readerFavouriteSeries.add(columns);
			} else if ("favourite-album".equals(localName)) {
				HashMap<String, String> columns = new HashMap<>();
				columns.put(FK_READER_ID, readerId);
				columns.put(FK_ALBUM_ID, attributes.getValue("ref"));
				readerFavouriteAlbums.add(columns);
			} else if ("reading-list-entry".equals(localName)) {
				HashMap<String, String> columns = new HashMap<>();
				columns.put(FK_READER_ID, readerId);
				columns.put(FK_ALBUM_ID, attributes.getValue("ref"));
				readerReadingList.add(columns);
			} else if ("configuration-entry".equals(localName)) {
				createLine("configuration", attributes);
			}
		}

		private void handleVersion(Attributes attributes) throws SAXException {
			int currentSchemaVersion = rawSqlDao.getSchemaVersion();
			Integer importSchemaVersion = null;
			for (int i = 0; i < attributes.getLength(); i++) {
				if ("schema".equals(attributes.getLocalName(i))) {
					importSchemaVersion = Integer.parseInt(attributes.getValue(i));
				}
			}
			if (importSchemaVersion == null || importSchemaVersion > currentSchemaVersion) {
				throw new SAXException(new DemyoException(DemyoErrorCode.IMPORT_WRONG_SCHEMA,
						"The imported file schema is at version " + importSchemaVersion
								+ " and seems to originate from an incompatible version of Demyo. "
								+ "This version of Demyo can only import schema versions " + currentSchemaVersion
								+ " and below."));
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
			} else if ("readers".equals(localName)) {
				readerId = null;
			} else if ("library".equals(localName)) {
				persistRelations();
			}
		}

		private void createLine(String tableName, Attributes attributes) {
			Map<String, String> attributeMap = new HashMap<>(attributes.getLength());
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
