package org.demyo.service.exporting;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.the4thlaw.commons.utils.io.FileUtils;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import org.demyo.common.exception.DemyoException;
import org.demyo.service.impl.AbstractServiceTest;
import org.demyo.test.assertions.Assert;
import org.demyo.test.assertions.xml.ElementAssert;

/**
 * Tests for {@link Demyo2Exporter}.
 */
@DatabaseSetup(value = "/org/demyo/test/demyo-dbunit-standard.xml", type = DatabaseOperation.REFRESH)
class Demyo2ExporterIT extends AbstractServiceTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(Demyo2ExporterIT.class);
	private static final String SAMPLE_HTML_DESCRIPTION = "<p>Sample HTML description</p>";

	@Autowired
	@Qualifier("demyo2Exporter")
	private IExporter exporter;

	/**
	 * Tests the completeness of a Demyo 2+ export.
	 *
	 * @throws DemyoException In case of export error.
	 * @throws IOException In case of I/O error.
	 */
	@Test
	void testExportCompleteness() throws DemyoException, IOException {
		Path expFile = exporter.export();

		String expContent = Files.readString(expFile, StandardCharsets.UTF_8);
		FileUtils.deleteQuietly(expFile);
		LOGGER.error(expContent);

		ElementAssert documentAssert = Assert.assertThatXml(expContent);

		/*
		 * TODO: The following properties still need to be checked
		 *
		 * AUTHORS
		 *  - pseudonyms
		 *  - date of birth
		 *  - date of death
		 *  - country
		 *  - website
		 *  - portrait
		 *
		 * PUBLISHER
		 *  - history
		 *
		 * COLLECTION
		 *  - feed
		 *  - history
		 *  - logo
		 *  - Album with collection_id
		 *
		 * SERIES
		 *  - Everything
		 *  - Including universe_id
		 *
		 * ALBUM
		 *  - All author types
		 *  - Original title
		 *  - Location
		 *
		 * TAXONS
		 *  - series-taxon
		 *
		 * BOOK TYPES
		 *  - description
		 *  - field_config
		 *
		 * UNIVERSES
		 *  - album universe
		 *  - logo
		 *  - description
		 *
		 * DERIVATIVES
		 *  - All properties
		 *
		 */

		// Images
		documentAssert.css("image")
				.hasSize(91)
				.byId(306)
				.exists()
				.hasAttribute("url", "dummy-image.jpg")
				.hasAttribute("description", "Lanfeust de Troy 5 - Couverture");
		documentAssert.xpathSingle("//album[@id=310]").hasAttribute("cover_id", "306");

		documentAssert.xpathSingle("//image[@id=309]")
				.hasAttribute("description", "Lanfeust de Troy 7 - Couverture (version n&b)");
		documentAssert.xpathSingle("//album[@id=312]/album-images/album-image")
				.hasAttribute("ref", "309");

		documentAssert.xpathSingle("//image[@id=139]")
				.hasAttribute("description", "Lanfeust de Troy 6 - Ex-Libris");
		documentAssert.xpathSingle("//derivative[@id=53]/derivative-images/derivative-image")
				.hasAttribute("ref", "139");

		// Publishers
		documentAssert.css("publisher")
				.hasSize(3)
				.byId(1)
				.hasAttribute("name", "Dargaud")
				.hasAttribute("website", "http://www.dargaud.com/")
				.hasAttribute("feed", "http://www.dargaud.com/actualites/news.aspx")
				.hasAttribute("logo_id", 5014);
		documentAssert.xpathSingle("//album[@id=306]")
				.hasAttribute("publisher_id", 4);
		documentAssert.xpathSingle("//collection[@id=90]")
				.hasAttribute("publisher_id", 1);

		// Collections
		documentAssert.css("collection")
				.hasSize(22)
				.byId(90)
				.hasAttribute("name", "Fictions")
				.hasAttribute("website", "http://www.dargaud.com/front/albums/collections/collection.aspx?id=954");

		// Bindings
		documentAssert.css("binding")
				.hasSize(2)
				.byId(1)
				.hasAttribute("name", "Cartonné");

		// Authors
		documentAssert.css("author")
				.hasSize(31)
				.byId(10)
				.hasAttribute("name", "Vatine")
				.hasAttribute("fname", "Olivier")
				.hasAttribute("biography", SAMPLE_HTML_DESCRIPTION);
		documentAssert.xpathSingle("//author[@id=216]")
				.hasAttribute("nickname", "Fred");

		// Taxons
		documentAssert.css("taxon")
				.hasSize(12)
				.byId(21)
				.hasAttribute("name", "one shot")
				.hasAttribute("taxon_type", "TAG");
		documentAssert.xpath("//album[@id=797]/album-taxons/album-taxon")
				.hasSize(2)
				.at(0)
				.hasAttribute("ref", 21)
				.siblings()
				.at(0)
				.hasAttribute("ref", 69);

		// Book types
		documentAssert.css("book_type")
				.hasSize(1)
				.byId(1)
				.hasAttribute("name", "__DEFAULT__")
				.hasAttribute("label_type", "GRAPHIC_NOVEL");
		documentAssert.xpathSingle("//album[@id=306]")
				.hasAttribute("book_type_id", 1);

		// Universes
		documentAssert.css("universe")
				.hasSize(2)
				.byId(8)
				.hasAttribute("name", "Lanfeust de Troy");

		// Albums
		documentAssert.css("album")
				.hasSize(77)
				.byId(306)
				.hasAttribute("series_id", 69)
				.hasAttribute("cycle", 1)
				.hasAttribute("number", "1.0")
				.hasAttribute("number_suffix", "ré")
				.hasAttribute("title", "L'Ivoire du Magohamoth")
				.hasAttribute("first_edition", "1994-10-01")
				.hasAttribute("this_edition", "1996-10-01")
				.hasAttribute("wishlist", false)
				.hasAttribute("binding_id", 1)
				.hasAttribute("cover_id", 302)
				.hasAttribute("comment", SAMPLE_HTML_DESCRIPTION)
				.hasAttribute("height", "320.0")
				.hasAttribute("width", "230.0")
				.hasAttribute("pages", 44)
				.hasAttribute("marked_as_first_edition", false);
		documentAssert.xpathSingle("//album[@id=1992]")
				.hasAttribute("printing", "2021-02-01");
		documentAssert.xpathSingle("//album[@id=796]")
				.hasAttribute("isbn", "978-2-30200-869-4")
				.hasAttribute("summary", SAMPLE_HTML_DESCRIPTION);

		documentAssert.source()
				// Album prices
				.contains("<album_price album_id=\"313\" date=\"2010-09-26\" price=\"15.0\" />")

				// Derivative types
				.contains("<derivative_type id=\"1\" name=\"Offset\" />")

				// Derivative prices
				.contains("<derivative_price derivative_id=\"53\" date=\"2010-09-26\" price=\"30.0\" />")

				// Readers
				.contains("<reader id=\"1\" name=\"My Reader\" colour=\"#304ffe\">")
				.contains("<favourite-series ref=\"169\" />")
				.contains("<favourite-album ref=\"1362\" />")
				.contains("<reading-list-entry ref=\"1515\" />")

				// Configuration
				.contains(
						"<configuration-entry id=\"9\" config_key=\"language\" config_value=\"fr\" reader_id=\"1\" />");

	}
}
