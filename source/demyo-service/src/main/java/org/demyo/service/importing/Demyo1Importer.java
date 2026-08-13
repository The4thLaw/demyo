package org.demyo.service.importing;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.the4thlaw.commons.services.io.IDirectoryService;
import org.the4thlaw.commons.utils.io.IOUtils;
import org.the4thlaw.commons.utils.io.Sniffer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import org.demyo.common.exception.DemyoException;
import org.demyo.dao.IRawSQLDao;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Importer for Demyo 1.5 files.
 */
@Component
public class Demyo1Importer extends Demyo2Importer {
	private static final Logger LOGGER = LoggerFactory.getLogger(Demyo1Importer.class);

	private static final Pattern FORMAT_PATTERN = Pattern.compile(".*<library [^>]*demyo-version=\"1\\..*\".*",
			Pattern.DOTALL);
	private static final String XSL_DTD_REMOVAL_PATTERN = "<!DOCTYPE doc \\[\\s*"
			+ "<!ATTLIST xsl:stylesheet id ID #REQUIRED>\\s*"
			+ "\\]>";
	private static final Pattern XSL_DTD_PRESENCE_PATTERN = Pattern
			.compile(".*<!ATTLIST xsl:stylesheet id ID #REQUIRED>.*", Pattern.DOTALL);

	public Demyo1Importer(IDirectoryService directoryService, IRawSQLDao rawSQLDao) {
		super(directoryService, rawSQLDao);
	}

	@Override
	protected String getZipExtension() {
		return "zip";
	}

	@Override
	protected Pattern getSniffPattern() {
		return FORMAT_PATTERN;
	}

	@Override
	protected void restoreImages(Path archiveDirectory, String imagesDirectoryName) throws DemyoException {
		// Demyo 1 had a specific directory for images, hardcode it here
		super.restoreImages(archiveDirectory, "collection_images");
	}

	@Override
	protected void parseAndImport(BufferedInputStream xmlBis, XMLReader xmlReader)
			throws IOException, SAXException, TransformerException {
		InputStream xslSheet = null;

		try {
			// Convert Demyo 1.5 to Demyo 2 on-the-fly
			xslSheet = Demyo1Importer.class.getResourceAsStream("demyo-to-demyo2.xsl");
			Source style = new StreamSource(xslSheet);
			TransformerFactory transFactory = TransformerFactory.newInstance();
			transFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
			transFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
			Transformer trans = transFactory.newTransformer(style);

			Source source = new SAXSource(xmlReader, new InputSource(xmlBis));
			Result result = new SAXResult(new Demyo2Handler(databaseDao, dataSource));

			// Transform and import
			trans.transform(source, result);
		} finally {
			IOUtils.closeQuietly(xslSheet);
		}
	}

	@Override
	protected void preProcessXml(Path xmlFile) throws IOException {
		stripXslDoctype(xmlFile);
	}

	/**
	 * Strips the doctype we know we wrote in Demyo 1 if it exists.
	 * <p>
	 * We only strip that one. DTDs are not allowed due to security concerns but we still want to be able to parse our
	 * own files.
	 * </p>
	 * <p>
	 * The regex seems restrictibe enough to avoid any security issues but we're still reading the file in memory so
	 * there's room for a DOS here in theory.
	 * </p>
	 *
	 * @param xmlFile The file to check
	 */

	private static void stripXslDoctype(Path xmlFile) throws IOException {
		if (Sniffer.sniffFile(xmlFile, XSL_DTD_PRESENCE_PATTERN)) {
			LOGGER.debug("{} contains an old Demyo 1.x DTD, removing it", xmlFile);

			byte[] content = Files.readAllBytes(xmlFile);
			String contentAsString = new String(content, UTF_8);

			contentAsString = Pattern.compile(XSL_DTD_REMOVAL_PATTERN, Pattern.DOTALL).matcher(contentAsString)
					.replaceFirst("");

			content = contentAsString.getBytes(UTF_8);
			Files.write(xmlFile, content);
		}
	}
}
