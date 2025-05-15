package org.demyo.service.importing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import org.demyo.common.exception.DemyoErrorCode;
import org.demyo.common.exception.DemyoException;
import org.demyo.dao.IRawSQLDao;

/**
 * SAX handler for import of Demyo 2.x files.
 */
public class Demyo2Handler extends DefaultHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(Demyo2Handler.class);

	private static final String FK_ALBUM_ID = "album_id";
	private static final String FK_READER_ID = "reader_id";

	private final IRawSQLDao rawSqlDao;

	private boolean hasBookType;
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
	private List<Map<String, String>> albumCoverArtists = new ArrayList<>();
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
	public Demyo2Handler(IRawSQLDao rawSqlDao) {
		this.rawSqlDao = rawSqlDao;
		allRelations.put("series_relations", relatedSeries);
		allRelations.put("albums_artists", albumArtists);
		allRelations.put("albums_writers", albumWriters);
		allRelations.put("albums_colorists", albumColorists);
		allRelations.put("albums_inkers", albumInkers);
		allRelations.put("albums_translators", albumTranslators);
		allRelations.put("albums_cover_artists", albumCoverArtists);
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
		switch (localName) {
			case "version":
				handleVersion(attributes);
				break;
			case "book_type":
				hasBookType = true;
				// Fall-through
			case "image", "publisher", "collection", "binding", "author", "tag", "borrower", "source",
					"derivative_type":
				createLine(localName + "s", attributes);
				break;
			case "series":
				seriesId = attributes.getValue("id");
				createLine("series", attributes);
				break;
			case "related_series":
				HashMap<String, String> columns = new HashMap<>();
				columns.put("main", seriesId);
				columns.put("sub", attributes.getValue("ref"));
				relatedSeries.add(columns);
				break;
			case "albums":
				if (!hasBookType) {
					// If by now we don't have a book type, we should create the default one
					// This behaviour is a bit peculiar but it's the first time we have a non-backwards compatible change
					// so let's deal with it the best we can
					LOGGER.info("Migrating an old import: the default book type will be created and assigned to all books");
					Map<String, Object> bookType = new HashMap<>();
					bookType.put("id", 1L);
					bookType.put("name", "__DEFAULT__");
					bookType.put("label_type", "GRAPHIC_NOVEL");
					createLine("book_types", bookType);
				}
				break;
			case "album":
				albumId = attributes.getValue("id");
				Map<String, String> albumAttributes = toMap(attributes);
				if (!hasBookType) {
					// Set the book type we just created
					albumAttributes.put("book_type_id", "1");
				}
				createLine("albums", albumAttributes);
				break;
			case "artist":
				albumArtists.add(join(FK_ALBUM_ID, albumId, "artist_id", attributes.getValue("ref")));
				break;
			case "writer":
				albumWriters.add(join(FK_ALBUM_ID, albumId, "writer_id", attributes.getValue("ref")));
				break;
			case "colorist":
				albumColorists.add(join(FK_ALBUM_ID, albumId, "colorist_id", attributes.getValue("ref")));
				break;
			case "inker":
				albumInkers.add(join(FK_ALBUM_ID, albumId, "inker_id", attributes.getValue("ref")));
				break;
			case "translator":
				albumTranslators.add(join(FK_ALBUM_ID, albumId, "translator_id", attributes.getValue("ref")));
				break;
			case "cover-artist":
				albumCoverArtists.add(join(FK_ALBUM_ID, albumId, "cover_artist_id", attributes.getValue("ref")));
				break;
			case "album-tag":
				albumTags.add(join(FK_ALBUM_ID, albumId, "tag_id", attributes.getValue("ref")));
				break;
			case "album-image":
				albumImages.add(join(FK_ALBUM_ID, albumId, "image_id", attributes.getValue("ref")));
				break;
			case "album_price":
				createLine("albums_prices", attributes);
				break;
			case "loan":
				createLine("albums_borrowers", attributes);
				break;
			case "derivative":
				derivativeId = attributes.getValue("id");
				createLine("derivatives", attributes);
				break;
			case "derivative-image":
				derivativeImages.add(join("derivative_id", derivativeId, "image_id", attributes.getValue("ref")));
				break;
			case "derivative_price":
				createLine("derivatives_prices", attributes);
				break;
			case "reader":
				readerId = attributes.getValue("id");
				createLine("readers", attributes);
				break;
			case "favourite-series":
				readerFavouriteSeries.add(join(FK_READER_ID, readerId, "series_id", attributes.getValue("ref")));
				break;
			case "favourite-album":
				readerFavouriteAlbums.add(join(FK_READER_ID, readerId, FK_ALBUM_ID, attributes.getValue("ref")));
				break;
			case "reading-list-entry":
				readerReadingList.add(join(FK_READER_ID, readerId, FK_ALBUM_ID, attributes.getValue("ref")));
				break;
			case "configuration-entry":
				createLine("configuration", attributes);
				break;
			default:
				// Do nothing
		}
	}

	private static Map<String, String> join(String col1, String val1, String col2, String val2) {
		HashMap<String, String> columns = new HashMap<>();
		columns.put(col1, val1);
		columns.put(col2, val2);
		return columns;
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
		switch (localName) {
			case "series":
				seriesId = null;
				break;
			case "albums":
				albumId = null;
				break;
			case "derivatives":
				derivativeId = null;
				break;
			case "readers":
				readerId = null;
				break;
			case "library":
				persistRelations();
				break;
			default:
				// No special behaviour for other tags
		}
	}

	private void createLine(String tableName, Attributes attributes) {
		Map<String, String> attributeMap = toMap(attributes);
		createLine(tableName, attributeMap);
	}

	private void createLine(String tableName, Map<String, ?> attributes) {
		rawSqlDao.insert(tableName, attributes);
	}

	private static Map<String, String> toMap(Attributes attributes) {
		Map<String, String> attributeMap = new HashMap<>(attributes.getLength());
		for (int i = 0; i < attributes.getLength(); i++) {
			attributeMap.put(attributes.getLocalName(i), attributes.getValue(i));
		}
		return attributeMap;
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
