package org.demyo.service.importing;

import java.io.BufferedInputStream;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.springframework.stereotype.Component;
import org.the4thlaw.commons.services.importing.BaseXmlImporter;
import org.the4thlaw.commons.services.io.IDirectoryService;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import org.demyo.dao.IRawSQLDao;

/**
 * Importer for Demyo 2.x files.
 */
@Component
public class Demyo2Importer extends BaseXmlImporter<IRawSQLDao> {
	private static final Pattern FORMAT_PATTERN = Pattern.compile(".*<library>.*<meta>.*<version.*", Pattern.DOTALL);

	protected final DataSource dataSource;

	public Demyo2Importer(IDirectoryService directoryService, IRawSQLDao rawSQLDao, DataSource dataSource) {
		super("Demyo", directoryService, rawSQLDao);
		this.dataSource = dataSource;
	}

	@Override
	protected String getZipExtension() {
		return "dea";
	}

	@Override
	protected Pattern getSniffPattern() {
		return FORMAT_PATTERN;
	}

	@Override
	protected void parseAndImport(BufferedInputStream xmlBis, XMLReader xmlReader) throws Exception {
		xmlReader.setContentHandler(new Demyo2Handler(databaseDao, dataSource));
		xmlReader.parse(new InputSource(xmlBis));
	}
}
