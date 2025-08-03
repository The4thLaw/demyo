package org.demyo.web.controller.api;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import org.demyo.common.config.SystemConfiguration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the export API.
 */
@DatabaseSetup(value = "/org/demyo/test/demyo-dbunit-standard.xml", type = DatabaseOperation.REFRESH)
class ExportAPIControllerIT extends AbstractAPIIT {
	private static final Logger LOGGER = LoggerFactory.getLogger(ExportAPIControllerIT.class);

	@BeforeAll
	static void setImageDirectory() throws IOException {
		Path imageDirectory = SystemConfiguration.getInstance().getImagesDirectory();
		try (InputStream samplePortrait1Stream = AbstractAPIIT.class.getResourceAsStream("/org/demyo/test/sample-image-portrait-1.jpg");
			InputStream sampleLandscape1Stream = AbstractAPIIT.class.getResourceAsStream("/org/demyo/test/sample-image-landscape-1.jpg")) {
			// TODO: implement this
		}
	}

	@Test
	void testExportDea() throws Exception {
		mockMvc.perform(get("/api/manage/export")
				.param("format", "XML")
				.param("withResources", "true"))
				.andDo(logResolvedException())
				.andExpect(status().isOk())
				.andExpect(header().string("Content-Type", "application/octet-stream"))
				.andDo(result -> {
					byte[] deaContent = result.getResponse().getContentAsByteArray();
					Path p = Path.of("target/export.dea");
					Files.write(p, deaContent);
					LOGGER.info("DEA export available in {}", p.toAbsolutePath());
				});
	}
}
