package org.demyo.service.exporting;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

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

			xsw.writeStartDocument();

			xsw.writeStartElement("library");

			writeMeta(xsw);

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

		xsw.writeStartElement("version");
		xsw.writeAttribute("demyo", "2.0.0-alpha3"); // TODO dynamic version
		xsw.writeAttribute("schema", "1"); // TODO dynamic version
		xsw.writeEndElement();

		xsw.writeStartElement("counts");
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
		xsw.writeStartElement(listTag);

		xsw.writeEndElement();
	}

	@Override
	public String getExtension() {
		return "xml";
	}
}
