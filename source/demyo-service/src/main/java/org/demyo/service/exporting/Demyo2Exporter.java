package org.demyo.service.exporting;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javanet.staxutils.IndentingXMLStreamWriter;

import javax.annotation.PostConstruct;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.demyo.common.config.SystemConfiguration;
import org.demyo.common.exception.DemyoErrorCode;
import org.demyo.common.exception.DemyoRuntimeException;
import org.demyo.dao.IRawSQLDao;
import org.demyo.service.IExportService;
import org.demyo.utils.io.DIOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * {@link IExporter} using the native Demyo 2 format.
 */
@Component
public class Demyo2Exporter implements IExporter {
	private static final Logger LOGGER = LoggerFactory.getLogger(Demyo2Exporter.class);

	private static ThreadLocal<DateFormat> DATE_FORMAT = new ThreadLocal<DateFormat>() {
		@Override
		protected DateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd");
		}
	};

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
	public File export() {
		LOGGER.debug("Starting export in Demyo 2 format");

		File out = SystemConfiguration.getInstance().createTempFile("demyo2-export-", ".xml");

		OutputStream outputStream = null;
		XMLStreamWriter xsw = null;
		try {
			outputStream = new FileOutputStream(out);

			xsw = XMLOutputFactory.newInstance().createXMLStreamWriter(
					new OutputStreamWriter(outputStream, "utf-8"));
			xsw = new IndentingXMLStreamWriter(xsw);

			xsw.writeStartDocument();

			xsw.writeStartElement("library");

			writeMeta(xsw);

			exportModel(xsw, "images", "image", "IMAGES");
			exportModel(xsw, "publishers", "publisher", "PUBLISHERS");
			exportModel(xsw, "collections", "collection", "COLLECTIONS");
			exportModel(xsw, "bindings", "binding", "BINDINGS");
			exportModel(xsw, "authors", "author", "AUTHORS");
			exportModel(xsw, "tags", "tag", "TAGS");

			// Series
			exportModel(xsw, "series-list", "series", "SERIES",
					new ManyToManyRelation[] { new ManyToManyRelation("related_series-list", "related_series",
							"MAIN", "SUB", rawSqlDao.getRawRecords("SERIES_RELATIONS")) });

			// Albums
			exportModel(
					xsw,
					"albums",
					"album",
					"ALBUMS",
					new ManyToManyRelation[] {
							new ManyToManyRelation("writers", "writer", "ALBUM_ID", "WRITER_ID", rawSqlDao
									.getRawRecords("ALBUMS_WRITERS")),
							new ManyToManyRelation("artists", "artist", "ALBUM_ID", "ARTIST_ID", rawSqlDao
									.getRawRecords("ALBUMS_ARTISTS")),
							new ManyToManyRelation("colorists", "colorist", "ALBUM_ID", "COLORIST_ID", rawSqlDao
									.getRawRecords("ALBUMS_COLORISTS")),
							new ManyToManyRelation("album-tags", "album-tag", "ALBUM_ID", "TAG_ID", rawSqlDao
									.getRawRecords("ALBUMS_TAGS")),
							new ManyToManyRelation("album-images", "album-image", "ALBUM_ID", "IMAGE_ID",
									rawSqlDao.getRawRecords("ALBUMS_IMAGES")) });

			exportModel(xsw, "album_prices", "album_price", "ALBUMS_PRICES");
			exportModel(xsw, "borrowers", "borrower", "BORROWERS");
			exportModel(xsw, "loan-history", "loan", "ALBUMS_BORROWERS");
			exportModel(xsw, "derivative_types", "derivative_type", "DERIVATIVE_TYPES");
			exportModel(xsw, "sources", "source", "SOURCES");

			// Derivatives
			exportModel(xsw, "derivatives", "derivative", "DERIVATIVES",
					new ManyToManyRelation[] { new ManyToManyRelation("derivative-images", "derivative-image",
							"DERIVATIVE_ID", "IMAGE_ID", rawSqlDao.getRawRecords("DERIVATIVES_IMAGES")) });

			exportModel(xsw, "derivative_prices", "derivative_price", "DERIVATIVES_PRICES");

			xsw.writeEndElement();

			xsw.writeEndDocument();

			xsw.close();
		} catch (IOException e) {
			throw new DemyoRuntimeException(DemyoErrorCode.EXPORT_IO_ERROR, e);
		} catch (XMLStreamException e) {
			throw new DemyoRuntimeException(DemyoErrorCode.EXPORT_XML_ERROR, e);
		} finally {
			DIOUtils.closeQuietly(xsw);
			DIOUtils.closeQuietly(outputStream);
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
		writeCount(xsw, "tags");

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

		for (Map<String, Object> record : records) {
			Number recordId = (Number) record.get("ID"); // By convention
			boolean hasRelations = hasRelations(relations, recordId);
			if (hasRelations) {
				xsw.writeStartElement(entityTag);
			} else {
				xsw.writeEmptyElement(entityTag);
			}

			// Write entity fields
			for (Entry<String, Object> field : record.entrySet()) {
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
		if (value instanceof Date) {
			return DATE_FORMAT.get().format((Date) value);
		}
		return value.toString();
	}

	@Override
	public String getExtension(boolean withResources) {
		return withResources ? "dea" : "xml";
	}
}
