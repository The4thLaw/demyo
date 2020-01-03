package org.demyo.utils.xml;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * General XML utilities.
 */
public final class XMLUtils {
	private XMLUtils() {

	}

	/**
	 * Creates an {@link XMLReader} from a {@link SAXParserFactory}, taking care of XEE protection.
	 * 
	 * @return The protected {@link XMLReader}
	 * @throws ParserConfigurationException In case of SAX configuration issue (protection and SAXParser creation).
	 * @throws SAXException In case of SAX issue (protection and SAXParser/XMLReader creation).
	 */
	public static XMLReader createXmlReader() throws ParserConfigurationException, SAXException {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		spf.setFeature("http://xml.org/sax/features/external-general-entities", false);
		spf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
		spf.setNamespaceAware(true);
		SAXParser saxParser = spf.newSAXParser();
		return saxParser.getXMLReader();
	}
}
