package org.demyo.service.importing;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.the4thlaw.commons.utils.io.FileUtils;
import org.the4thlaw.commons.utils.io.IOUtils;
import org.the4thlaw.commons.utils.io.Sniffer;
import org.the4thlaw.commons.utils.xml.XMLUtils;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import org.demyo.common.exception.DemyoErrorCode;
import org.demyo.common.exception.DemyoException;

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

	@Override
	public boolean supports(String originalFilename, Path file) throws DemyoException {
		String originalFilenameLc = originalFilename.toLowerCase();

		if (originalFilenameLc.endsWith(".xml")) {
			return Sniffer.sniffFile(file, FORMAT_PATTERN);
		}

		return originalFilenameLc.endsWith(".zip");
	}

	@Override
	public void importFile(String originalFilename, Path file) throws DemyoException {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		Path archiveDirectory = null;
		InputStream xslSheet = null;
		InputStream xmlFis = null;
		BufferedInputStream xmlBis = null;

		try {
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("Starting import, file size is {} bytes", Files.size(file));
			}

			// Extract if needed
			String originalFilenameLc = originalFilename.toLowerCase();
			boolean isArchive = originalFilenameLc.endsWith(".zip");

			Path xmlFile;
			if (isArchive) {
				archiveDirectory = extractZip(file);
				xmlFile = archiveDirectory.resolve("demyo.xml");
			} else {
				xmlFile = file;
			}

			stopWatch.split();
			long splitTime = stopWatch.getSplitDuration().toMillis();

			stripXslDoctype(xmlFile);

			// Create a SAX parser for the input file
			XMLReader xmlReader = XMLUtils.createXmlReader();

			// Convert Demyo 1.5 to Demyo 2 on-the-fly
			xslSheet = Demyo1Importer.class.getResourceAsStream("demyo-to-demyo2.xsl");
			Source style = new StreamSource(xslSheet);
			TransformerFactory transFactory = TransformerFactory.newInstance();
			transFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
			transFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
			Transformer trans = transFactory.newTransformer(style);

			// Input is the XML from Demyo 1.5, output is a bridge to the Demyo 2.x SAX parser
			xmlFis = Files.newInputStream(xmlFile);
			xmlBis = new BufferedInputStream(xmlFis);
			Source source = new SAXSource(xmlReader, new InputSource(xmlBis));
			Result result = new SAXResult(new Demyo2Handler(rawSqlDao, dataSource));

			// Transform and import
			trans.transform(source, result);

			stopWatch.split();
			splitTime = stopWatch.getSplitDuration().toMillis() - splitTime;

			// Move extracted images to the right directory
			if (isArchive) {
				restoreImages(archiveDirectory, "collection_images");
			}
			stopWatch.stop();

			LOGGER.info("Import took {}ms: {}ms in database and {}ms in I/O operations", stopWatch.getTime(), splitTime,
					stopWatch.getTime() - splitTime);
		} catch (IOException ioe) {
			throw new DemyoException(DemyoErrorCode.IMPORT_IO_ERROR, ioe);
		} catch (SAXException | TransformerException | ParserConfigurationException e) {
			throw new DemyoException(DemyoErrorCode.IMPORT_PARSE_ERROR, e);
		} finally {
			IOUtils.closeQuietly(xslSheet);
			IOUtils.closeQuietly(xmlBis);
			IOUtils.closeQuietly(xmlFis);
			FileUtils.deleteDirectoryQuietly(archiveDirectory);
		}
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
