package org.demyo.service.exporting;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
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
		// System.out.println(expContent);

		ElementAssert documentAssert = Assert.assertThatXml(expContent);

		/*
		 * TODO: The following properties still need to be checked
		 *
		 * SERIES
		 *  - Everything
		 *  - Including universe_id*
		 *
		 * ALBUM
		 *  - All author types
		 *
		 * DERIVATIVES
		 *  - All properties
		 *
		 */

		// Images
		documentAssert.css("image")
				.hasSize(140)
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
				.hasSize(5)
				.byId(1)
				.hasAttribute("name", "Dargaud")
				.hasAttribute("website", "http://www.dargaud.com/")
				.hasAttribute("feed", "http://www.dargaud.com/actualites/news.aspx")
				.hasAttribute("logo_id", 5014);
		documentAssert.xpathSingle("//album[@id=306]")
				.hasAttribute("publisher_id", 4);
		documentAssert.xpathSingle("//publisher[@id=4]")
				.hasAttribute("history", SAMPLE_HTML_DESCRIPTION);

		// Collections
		// Note: can't test the feed, noone offers it anymore at this level
		documentAssert.css("collection")
				.hasSize(22)
				.byId(90)
				.hasAttribute("name", "Fictions")
				.hasAttribute("website", "http://www.dargaud.com/front/albums/collections/collection.aspx?id=954");
		documentAssert.xpathSingle("//collection[@id=38]")
				.hasAttribute("history", SAMPLE_HTML_DESCRIPTION);
		documentAssert.xpathSingle("//collection[@id=31]")
				.hasAttribute("logo_id", 7083);
		documentAssert.xpathSingle("//collection[@id=90]")
				.hasAttribute("publisher_id", 1);

		// Bindings
		documentAssert.css("binding")
				.hasSize(3)
				.byId(1)
				.hasAttribute("name", "Cartonné");

		// Authors
		documentAssert.css("author")
				.hasSize(37)
				.byId(119)
				.hasAttribute("name", "Tarquin")
				.hasAttribute("fname", "Didier")
				.hasAttribute("biography", SAMPLE_HTML_DESCRIPTION);
		documentAssert.xpathSingle("//author[@id=216]")
				.hasAttribute("nickname", "Fred");
		documentAssert.xpathSingle("//author[@id=282]")
				.hasAttribute("birth", "1937-03-14")
				.hasAttribute("death", "1976-01-21")
				.hasAttribute("country", "BEL");
		documentAssert.xpathSingle("//author[@id=311]")
				.hasAttribute("website", "https://seblamirand.blogspot.com/");
		documentAssert.xpathSingle("//author[@id=120]")
				.hasAttribute("portrait_id", 6737);
		documentAssert.xpathSingle("//author[@id=658]")
				.hasAttribute("pseudonym_of_id", 120);

		// Taxons
		documentAssert.css("taxon")
				.hasSize(19)
				.byId(21)
				.hasAttribute("name", "one shot")
				.hasAttribute("taxon_type", "TAG");
		documentAssert.xpath("//album[@id=653]/album-taxons/album-taxon")
				.hasSize(3)
				.at(0)
				.hasAttribute("ref", 5)
				.siblings()
				.at(0)
				.hasAttribute("ref", 10)
				.siblings()
				.at(1)
				.hasAttribute("ref", 128);
		documentAssert.xpath("//series[@id=69]/series-taxons/series-taxon")
				.hasSize(1)
				.at(0)
				.hasAttribute("ref", 5);

		// Book types
		documentAssert.css("book_type")
				.hasSize(3)
				.byId(3)
				.hasAttribute("name", "Roman")
				.hasAttribute("label_type", "NOVEL")
				.hasAttribute("description", SAMPLE_HTML_DESCRIPTION)
				.hasAttribute("field_config", "ALBUM_COLORIST,ALBUM_INKER");
		documentAssert.xpathSingle("//album[@id=306]")
				.hasAttribute("book_type_id", 1);

		// Universes
		documentAssert.css("universe")
				.hasSize(3)
				.byId(8)
				.hasAttribute("name", "Mondes de Troy")
				.hasAttribute("logo_id", 7085);
		documentAssert.xpathSingle("//universe[@id=30]")
		.hasAttribute("description", SAMPLE_HTML_DESCRIPTION);
		// TODO: add other images, find an album with a universe

		// Albums
		documentAssert.css("album")
				.hasSize(93)
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
		documentAssert.xpathSingle("//album[@id=2892]")
				.hasAttribute("location", "Mezzanine, D3");
		documentAssert.xpathSingle("//album[@id=3962]")
				.hasAttribute("original_title", "Πλάτωνος Ἀπολογία Σωκράτους");
		documentAssert.xpathSingle("//album[@id=1024]")
				.hasAttribute("collection_id", 33);

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
