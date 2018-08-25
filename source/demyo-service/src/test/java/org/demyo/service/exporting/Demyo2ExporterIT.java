package org.demyo.service.exporting;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import org.demyo.common.exception.DemyoException;
import org.demyo.service.impl.AbstractServiceTest;

/**
 * Tests for {@link Demyo2Exporter}.
 */
@DatabaseSetup(value = "/org/demyo/test/demyo-dbunit-standard.xml", type = DatabaseOperation.REFRESH)
public class Demyo2ExporterIT extends AbstractServiceTest {
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
	// TODO: Add more checks
	@Test
	public void testExportCompleteness() throws DemyoException, IOException {
		File expFile = exporter.export();
		String expContent = FileUtils.readFileToString(expFile, StandardCharsets.UTF_8);
		expFile.delete();
		LOGGER.error(expContent);

		assertThat(expContent).contains("<reader id=\"1\" name=\"Demyo\"/>");
		assertThat(expContent).contains("<reader id=\"2\" name=\"Sarah\" colour=\"#FF69B4\">");
		assertThat(expContent).contains("<favourite-series ref=\"1\"/>");
		assertThat(expContent).contains("<reading-list-entry ref=\"2\"/>");

	}
}
