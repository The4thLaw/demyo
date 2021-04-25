package org.demyo.service.exporting;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import org.demyo.common.exception.DemyoException;
import org.demyo.service.impl.AbstractServiceTest;
import org.demyo.utils.io.DIOUtils;

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
		File expFile = exporter.export();
		String expContent = FileUtils.readFileToString(expFile, StandardCharsets.UTF_8);
		DIOUtils.delete(expFile);
		LOGGER.error(expContent);

		assertThat(expContent)
				// Album assertions
				.containsPattern(".*<album id=\"1313\".*printing=\"2015-09-01\".*>")
				// Tag assertions
				.contains("<tag id=\"1\" name=\"science-fiction\" description=\"SF\"/>")
				// Reader assertions
				.contains("<reader id=\"1\" name=\"Xavier\" colour=\"#304ffe\">")
				.contains("<favourite-series ref=\"99\"/>")
				.contains("<favourite-album ref=\"1313\"/>")
				.contains("<reading-list-entry ref=\"1459\"/>");

	}
}
