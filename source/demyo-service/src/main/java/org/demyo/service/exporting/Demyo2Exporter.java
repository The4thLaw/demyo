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
import org.demyo.service.impl.IExportService;
import org.demyo.service.impl.IExporter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

// TODO: use https://ewernli.wordpress.com/2009/06/18/stax-pretty-printer/
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

		try {
			OutputStream outputStream = new FileOutputStream(out);

			XMLStreamWriter xsw = XMLOutputFactory.newInstance().createXMLStreamWriter(
					new OutputStreamWriter(outputStream, "utf-8"));
			xsw = new IndentingXMLStreamWriter(xsw);

			xsw.writeStartDocument();

			xsw.writeStartElement("library");

			writeMeta(xsw);

			exportModel(xsw, "images", "image", "IMAGES");
			exportModel(xsw, "publishers", "publisher", "PUBLISHERS");
			exportModel(xsw, "collections", "collection", "COLLECTIONS");
			exportModel(xsw, "bindings", "binding", "BINDINGS");
			// authors
			// tags
			// series-list / series (with self habtm for <related_series>			<series ref="165"/>
			// albums (with many habtm)
			// album_prices / album_price
			// borrowers / borrower
			// loan-history / loan
			// derivative_types
			// sources / source
			// derivatives (with habtm)
			// derivative_prices

			xsw.writeEndElement();

			xsw.writeEndDocument();

			xsw.close();
		} catch (IOException e) {
			throw new DemyoRuntimeException(DemyoErrorCode.EXPORT_IO_ERROR, e);
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
		// TODO: proper error management

		return out;
	}

	private void writeMeta(XMLStreamWriter xsw) throws XMLStreamException {
		xsw.writeStartElement("meta");

		xsw.writeEmptyElement("version");
		xsw.writeAttribute("demyo", "2.0.0-alpha3"); // TODO dynamic version
		xsw.writeAttribute("schema", "1"); // TODO dynamic version

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

	private void exportModel(XMLStreamWriter xsw, String listTag, String entityTag, String tableName)
			throws XMLStreamException {
		List<Map<String, Object>> records = rawSqlDao.getRawRecords(tableName);

		if (records.isEmpty()) {
			return;
		}

		xsw.writeStartElement(listTag);

		for (Map<String, Object> record : records) {
			xsw.writeEmptyElement(entityTag);
			for (Entry<String, Object> field : record.entrySet()) {
				if (field.getValue() != null) {
					xsw.writeAttribute(field.getKey().toLowerCase(), toString(field.getValue()));
				}
			}
		}

		xsw.writeEndElement();
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
	public String getExtension() {
		return "xml";
	}
}
