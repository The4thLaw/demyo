package org.demyo.service.exporting;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.the4thlaw.commons.utils.xml.XMLUtils;

import org.demyo.common.config.SystemConfiguration;
import org.demyo.common.exception.DemyoErrorCode;
import org.demyo.common.exception.DemyoException;
import org.demyo.dao.IRawSQLDao;
import org.demyo.service.IExportService;

import javanet.staxutils.IndentingXMLStreamWriter;

/**
 * {@link IExporter} using the native Demyo 2 format.
 */
@Component
public class Demyo2Exporter implements IExporter {
	private static final String SERIES_KEY = "SERIES_ID";
	private static final String READER_KEY = "READER_ID";
	private static final String ALBUM_KEY = "ALBUM_ID";

	private static final Logger LOGGER = LoggerFactory.getLogger(Demyo2Exporter.class);

	private static final ThreadLocal<DateFormat> DATE_FORMAT = ThreadLocal
			.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));

	@Autowired
	private IExportService exportService;
	@Autowired
	private IRawSQLDao rawSqlDao;

	@PostConstruct
	private void init() {
		exportService.registerExporter(this);
	}

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public Path export() throws DemyoException {
		LOGGER.debug("Starting export in Demyo 2 format");

		Path out = SystemConfiguration.getInstance().createTempFile("demyo2-export-", ".xml");

		XMLStreamWriter xsw = null;
		try (OutputStream outputStream = Files.newOutputStream(out)) {

			xsw = XMLOutputFactory.newInstance().createXMLStreamWriter(outputStream, StandardCharsets.UTF_8.toString());
			xsw = new IndentingXMLStreamWriter(xsw);

			xsw.writeStartDocument();

			xsw.writeStartElement("library");

			writeMeta(xsw);

			exportModel(xsw, "images", "image", "IMAGES");
			exportModel(xsw, "publishers", "publisher", "PUBLISHERS");
			exportModel(xsw, "collections", "collection", "COLLECTIONS");
			exportModel(xsw, "bindings", "binding", "BINDINGS");
			exportModel(xsw, "authors", "author", "AUTHORS");
			exportModel(xsw, "taxons", "taxon", "TAXONS");
			exportModel(xsw, "book_types", "book_type", "BOOK_TYPES");
			exportModel(xsw, "universes", "universe", "UNIVERSES");
			exportModel(xsw, "series-list", "series", "SERIES",
					new ManyToManyRelation("series-taxons", "series-taxon", ALBUM_KEY, "TAXON_ID",
							rawSqlDao.getRawRecords("SERIES_TAXONS")));

			// Albums
			exportModel(xsw, "albums", "album", "ALBUMS",
					new ManyToManyRelation("writers", "writer", ALBUM_KEY, "WRITER_ID",
							rawSqlDao.getRawRecords("ALBUMS_WRITERS")),
					new ManyToManyRelation("artists", "artist", ALBUM_KEY, "ARTIST_ID",
							rawSqlDao.getRawRecords("ALBUMS_ARTISTS")),
					new ManyToManyRelation("colorists", "colorist", ALBUM_KEY, "COLORIST_ID",
							rawSqlDao.getRawRecords("ALBUMS_COLORISTS")),
					new ManyToManyRelation("inkers", "inker", ALBUM_KEY, "INKER_ID",
							rawSqlDao.getRawRecords("ALBUMS_INKERS")),
					new ManyToManyRelation("translators", "translator", ALBUM_KEY, "TRANSLATOR_ID",
							rawSqlDao.getRawRecords("ALBUMS_TRANSLATORS")),
					new ManyToManyRelation("cover-artists", "cover-artist", ALBUM_KEY, "COVER_ARTIST_ID",
							rawSqlDao.getRawRecords("ALBUMS_COVER_ARTISTS")),
					new ManyToManyRelation("album-taxons", "album-taxon", ALBUM_KEY, "TAXON_ID",
							rawSqlDao.getRawRecords("ALBUMS_TAXONS")),
					new ManyToManyRelation("album-images", "album-image", ALBUM_KEY, "IMAGE_ID",
							rawSqlDao.getRawRecords("ALBUMS_IMAGES")));

			exportModel(xsw, "album_prices", "album_price", "ALBUMS_PRICES");
			exportModel(xsw, "borrowers", "borrower", "BORROWERS");
			exportModel(xsw, "loan-history", "loan", "ALBUMS_BORROWERS");
			exportModel(xsw, "derivative_types", "derivative_type", "DERIVATIVE_TYPES");
			exportModel(xsw, "sources", "source", "SOURCES");

			// Derivatives
			exportModel(xsw, "derivatives", "derivative", "DERIVATIVES",
					new ManyToManyRelation("derivative-images", "derivative-image", "DERIVATIVE_ID", "IMAGE_ID",
							rawSqlDao.getRawRecords("DERIVATIVES_IMAGES")));

			exportModel(xsw, "derivative_prices", "derivative_price", "DERIVATIVES_PRICES");

			// Readers
			exportModel(xsw, "readers", "reader", "READERS",
					new ManyToManyRelation("favourite-series-list", "favourite-series", READER_KEY, SERIES_KEY,
							rawSqlDao.getRawRecords("READERS_FAVOURITE_SERIES")),
					new ManyToManyRelation("favourite-albums", "favourite-album", READER_KEY, ALBUM_KEY,
							rawSqlDao.getRawRecords("READERS_FAVOURITE_ALBUMS")),
					new ManyToManyRelation("reading-list", "reading-list-entry", READER_KEY, ALBUM_KEY,
							rawSqlDao.getRawRecords("READERS_READING_LIST")));

			exportModel(xsw, "configuration", "configuration-entry", "CONFIGURATION");

			xsw.writeEndElement();

			xsw.writeEndDocument();

			xsw.close();
		} catch (IOException e) {
			throw new DemyoException(DemyoErrorCode.EXPORT_IO_ERROR, e);
		} catch (XMLStreamException e) {
			throw new DemyoException(DemyoErrorCode.EXPORT_XML_ERROR, e);
		} finally {
			// We will most likely not need this anymore in the current request. Clear it to avoid leaks.
			DATE_FORMAT.remove();

			XMLUtils.closeQuietly(xsw);
		}

		return out;
	}

	private void writeMeta(XMLStreamWriter xsw) throws XMLStreamException {
		xsw.writeStartElement("meta");

		xsw.writeEmptyElement("version");
		xsw.writeAttribute("demyo", SystemConfiguration.getInstance().getVersion());
		xsw.writeAttribute("schema", String.valueOf(rawSqlDao.getSchemaVersion()));

		xsw.writeEmptyElement("counts");
		writeCount(xsw, "albums");
		writeCount(xsw, "albums_prices");
		writeCount(xsw, "authors");
		writeCount(xsw, "bindings");
		writeCount(xsw, "book_types");
		writeCount(xsw, "borrowers");
		writeCount(xsw, "collections");
		writeCount(xsw, "derivatives");
		writeCount(xsw, "derivative_types");
		writeCount(xsw, "derivatives_prices");
		writeCount(xsw, "images");
		writeCount(xsw, "albums_borrowers", "loan-history");
		writeCount(xsw, "publishers");
		writeCount(xsw, "series");
		writeCount(xsw, "sources");
		writeCount(xsw, "taxons");

		xsw.writeEndElement();
	}

	private void writeCount(XMLStreamWriter xsw, String tableName) throws XMLStreamException {
		writeCount(xsw, tableName, tableName);
	}

	private void writeCount(XMLStreamWriter xsw, String tableName, String alias) throws XMLStreamException {
		xsw.writeAttribute(alias, Long.toString(rawSqlDao.count(tableName)));
	}

	private void exportModel(XMLStreamWriter xsw, String listTag, String entityTag, String tableName,
			ManyToManyRelation... relations) throws XMLStreamException {
		List<Map<String, Object>> records = rawSqlDao.getRawRecords(tableName);

		if (records.isEmpty()) {
			return;
		}

		xsw.writeStartElement(listTag);

		for (Map<String, Object> rec : records) {
			Number recordId = (Number) rec.get("ID"); // By convention
			boolean hasRelations = hasRelations(relations, recordId);
			if (hasRelations) {
				xsw.writeStartElement(entityTag);
			} else {
				xsw.writeEmptyElement(entityTag);
			}

			// Write entity fields
			for (Entry<String, Object> field : rec.entrySet()) {
				if (field.getValue() != null) {
					xsw.writeAttribute(field.getKey().toLowerCase(), toString(field.getValue()));
				}
			}

			// Write relations
			for (ManyToManyRelation rel : relations) {
				rel.writeRelationToStream(recordId, xsw);
			}

			if (hasRelations) {
				xsw.writeEndElement();
			}
		}

		xsw.writeEndElement();
	}

	private static boolean hasRelations(ManyToManyRelation[] relations, Number recordId) {
		for (ManyToManyRelation rel : relations) {
			if (rel.hasRelations(recordId)) {
				return true;
			}
		}
		return false;
	}

	private static String toString(Object value) {
		if (value == null) {
			return "";
		}
		if (value instanceof Date asDate) {
			return DATE_FORMAT.get().format(asDate);
		}
		return value.toString();
	}

	@Override
	public String getExtension(boolean withResources) {
		return withResources ? "dea" : "xml";
	}
}
