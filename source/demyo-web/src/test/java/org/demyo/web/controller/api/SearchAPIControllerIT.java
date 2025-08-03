package org.demyo.web.controller.api;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for {@link SearchAPIController}.
 */
@DatabaseSetup(value = "/org/demyo/test/demyo-dbunit-standard.xml", type = DatabaseOperation.REFRESH)
class SearchAPIControllerIT extends AbstractAPIIT {
	/**
	 * Tests {@link SearchAPIController#quickSearch(String)}
	 */
	@Test
	void quicksearch() throws Exception {
		mockMvc.perform(get("/api/search/quick?q=codex"))
				.andExpect(status().isOk())
				.andDo(MockMvcResultHandlers.print())
				.andExpect(jsonPath("$.albums").doesNotExist())
				.andExpect(jsonPath("$.series", hasSize(1)))
				.andExpect(jsonPath("$.series[0].identifyingName").value("Codex de Troy"));

		mockMvc.perform(get("/api/search/quick?q=than"))
				.andExpect(status().isOk())
				.andDo(MockMvcResultHandlers.print())
				.andExpect(jsonPath("$.albums[0].series.identifyingName").value("Lanfeust de Troy"))
				.andExpect(jsonPath("$.albums[0].title").value("Thanos l'Incongru"));

	}
}
