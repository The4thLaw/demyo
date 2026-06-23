package org.demyo.web.controller.api;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the export API.
 */
@DatabaseSetup(value = "/org/demyo/test/demyo-dbunit-standard.xml", type = DatabaseOperation.REFRESH)
class ExportAPIControllerIT extends AbstractAPIIT {
	private static final Logger LOGGER = LoggerFactory.getLogger(ExportAPIControllerIT.class);

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
