package org.demyo.service.importing;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import org.demyo.common.exception.DemyoException;

/**
 * Tests for {@link Demyo1Importer}.
 */
public class Demyo1ImporterTest {

	/**
	 * Tests {@link Demyo1Importer#supports(String, Path)} for a simple 1.5 file.
	 * 
	 * @throws IOException In case of support error.
	 * @throws DemyoException In case of I/O error while writing or reading the sample file.
	 */
	@Test
	void testSupportsSimple() throws IOException, DemyoException {
		String sampleHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<library demyo-version=\"1.5\"";
		assertSupportsFile(sampleHeader);
	}

	/**
	 * Tests {@link Demyo1Importer#supports(String, Path)} for a 1.4 file with an embedded XSL.
	 * 
	 * @throws IOException In case of support error.
	 * @throws DemyoException In case of I/O error while writing or reading the sample file.
	 */
	@Test
	void testSupportsHollisterWithXSL() throws IOException, DemyoException {
		String sampleHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "<?xml-stylesheet href=\"#style\" type=\"text/xsl\"?>\n"
				+ "<!DOCTYPE doc [\n"
				+ "<!ATTLIST xsl:stylesheet id ID #REQUIRED>\n"
				+ "]>\n"
				+ "<library xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" "
				+ "xmlns:default=\"http://www.w3.org/1999/xhtml\" demyo-version=\"1.4\" "
				+ "schema-version=\"3\" entries=\"10864\">";
		assertSupportsFile(sampleHeader);
	}

	private void assertSupportsFile(String sampleHeader) throws IOException, DemyoException {
		Path tempFile = null;
		try {
			tempFile = Files.createTempFile("Demyo1ImporterTest", ".xml");
			Files.write(tempFile, sampleHeader.getBytes(StandardCharsets.UTF_8));

			Demyo1Importer instance = new Demyo1Importer();

			assertThat(instance.supports(tempFile.getFileName().toString(), tempFile)).isTrue();
		} finally {
			if (tempFile != null && Files.exists(tempFile)) {
				Files.delete(tempFile);
			}
		}
	}
}
