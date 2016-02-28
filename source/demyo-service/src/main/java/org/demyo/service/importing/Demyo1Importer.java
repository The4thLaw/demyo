package org.demyo.service.importing;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

import org.demyo.common.exception.DemyoErrorCode;
import org.demyo.common.exception.DemyoException;
import org.demyo.service.IImportService;
import org.demyo.utils.io.DIOUtils;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Importer for Demyo 1.5 files.
 */
@Component
public class Demyo1Importer extends Demyo2Importer {
	private static final Logger LOGGER = LoggerFactory.getLogger(Demyo1Importer.class);

	@Autowired
	private IImportService importService;

	// Registering to the import service is inherited from parent class

	@Override
	public boolean supports(String originalFilename, File file) throws DemyoException {
		String originalFilenameLc = originalFilename.toLowerCase();

		if (originalFilenameLc.endsWith(".xml")) {
			return DIOUtils.sniffFile(file,
					Pattern.compile(".*<library demyo-version=\"1\\..*\".*", Pattern.DOTALL));
		}

		return originalFilenameLc.endsWith(".zip");
	}

	@Override
	public void importFile(String originalFilename, File file) throws DemyoException {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		File archiveDirectory = null;
		InputStream xslSheet = null;
		FileInputStream xmlFis = null;
		BufferedInputStream xmlBis = null;

		try {
			// Extract if needed
			String originalFilenameLc = originalFilename.toLowerCase();
			boolean isArchive = originalFilenameLc.endsWith(".zip");

			File xmlFile;
			if (isArchive) {
				try {
					archiveDirectory = extractZip(file);
				} catch (IOException e) {
					throw new DemyoException(DemyoErrorCode.IMPORT_IO_ERROR, e);
				}
				xmlFile = new File(archiveDirectory, "demyo.xml");
			} else {
				xmlFile = file;
			}

			stopWatch.split();
			long splitTime = stopWatch.getSplitTime();

			// Create a SAX parser for the input file
			SAXParserFactory spf = SAXParserFactory.newInstance();
			spf.setNamespaceAware(true);
			SAXParser saxParser = spf.newSAXParser();
			XMLReader xmlReader = saxParser.getXMLReader();

			// Convert Demyo 1.5 to Demyo 2 on-the-fly
			xslSheet = Demyo1Importer.class.getResourceAsStream("demyo-to-demyo2.xsl");
			Source style = new StreamSource(xslSheet);
			TransformerFactory transFactory = TransformerFactory.newInstance();
			Transformer trans = transFactory.newTransformer(style);

			// Input is the XML from Demyo 1.5, output is a bridge to the Demyo 2.x SAX parser
			xmlFis = new FileInputStream(xmlFile);
			xmlBis = new BufferedInputStream(xmlFis);
			Source source = new SAXSource(xmlReader, new InputSource(xmlBis));
			Result result = new SAXResult(new Demyo2Handler());

			// Transform and import
			trans.transform(source, result);

			stopWatch.split();
			splitTime = stopWatch.getSplitTime() - splitTime;

			// Move extracted images to the right directory
			if (isArchive) {
				restoreImages(archiveDirectory, "collection_images");
			}
			stopWatch.stop();

			LOGGER.info("Import took {}ms: {}ms in database and {}ms in I/O operations", stopWatch.getTime(),
					splitTime, stopWatch.getTime() - splitTime);
		} catch (IOException ioe) {
			throw new DemyoException(DemyoErrorCode.IMPORT_IO_ERROR, ioe);
		} catch (SAXException | TransformerException | ParserConfigurationException e) {
			throw new DemyoException(DemyoErrorCode.IMPORT_PARSE_ERROR, e);
		} finally {
			DIOUtils.closeQuietly(xslSheet);
			DIOUtils.closeQuietly(xmlBis);
			DIOUtils.closeQuietly(xmlFis);
			DIOUtils.deleteDirectory(archiveDirectory);
		}
	}
}
