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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link Demyo2Exporter}.
 */
@DatabaseSetup(value = "/org/demyo/test/demyo-dbunit-standard.xml", type = DatabaseOperation.REFRESH)
class Demyo2ExporterIT extends AbstractServiceTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(Demyo2ExporterIT.class);

	@Autowired
	@Qualifier("demyo2Exporter")
	private IExporter exporter;

	/**
	 * Tests that the export does not miss anything.
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

		assertThat(expContent)
				// Book type assertions
				.contains("<book_type id=\"1\" name=\"Bande dessinée\" description=\"Pour toutes les BDs\" label_type=\"GRAPHIC_NOVEL\"/>")
				// Album assertions
				.containsPattern(".*<album id=\"1313\" .*printing=\"2015-09-01\" .*book_type_id=\"1\".*>")
				// Tag assertions
				.contains("<tag id=\"1\" name=\"science-fiction\" description=\"SF\"/>")
				// Reader assertions
				.contains("<reader id=\"1\" name=\"Xavier\" colour=\"#304ffe\">")
				.contains("<favourite-series ref=\"99\"/>")
				.contains("<favourite-album ref=\"1313\"/>")
				.contains("<reading-list-entry ref=\"1459\"/>");

	}
}
