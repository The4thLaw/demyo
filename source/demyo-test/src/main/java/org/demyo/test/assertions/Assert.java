package org.demyo.test.assertions;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

import org.demyo.test.assertions.xml.ElementAssert;

/**
 * Custom assertions.
 */
public final class Assert {
	private Assert() {
		// Utility class
	}

	/**
	 * Prepare assertions about an XML document.
	 * @param xml The document source.
	 * @return The asserter.
	 */
	public static ElementAssert assertThatXml(String xml) {
		Document doc = Jsoup.parse(xml, "", Parser.xmlParser());
		return new ElementAssert(doc);
	}
}
