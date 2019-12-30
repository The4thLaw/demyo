package org.demyo.utils.xml;

import static org.assertj.core.api.Assertions.assertThat;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Tests for {@link XMLUtils}.
 */
public class XMLUtilsTest {
	/**
	 * Tests {@link XMLUtils#createXmlReader()}.
	 * 
	 * @throws ParserConfigurationException In case of test issue
	 * @throws SAXException In case of test issue
	 */
	@Test
	public void createXmlReader() throws ParserConfigurationException, SAXException {
		assertThat(XMLUtils.createXmlReader()).isNotNull();
	}
}
