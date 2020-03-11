package org.demyo.web.controller.api;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

/**
 * Tests for {@link SearchAPIController}.
 */
@DatabaseSetup(value = "/org/demyo/test/demyo-dbunit-standard.xml", type = DatabaseOperation.REFRESH)
public class SearchAPIControllerIT extends AbstractAPIIT {
	/**
	 * Tests {@link SearchAPIController#quickSearch(String)}
	 */
	@Test
	public void quicksearch() throws Exception {
		mockMvc.perform(get("/api/search/quick?q=sill"))
				.andExpect(status().isOk())
				.andDo(MockMvcResultHandlers.print())
				.andExpect(jsonPath("$.albums").doesNotExist())
				.andExpect(jsonPath("$.series", hasSize(2)))
				.andExpect(jsonPath("$.series[0].identifyingName").value("Sillage"));

		mockMvc.perform(get("/api/search/quick?q=feu"))
				.andExpect(status().isOk())
				.andDo(MockMvcResultHandlers.print())
				.andExpect(jsonPath("$.albums[0].series.identifyingName").value("Sillage"))
				.andExpect(jsonPath("$.albums[0].title").value("À Feu et à Cendres"));

	}
}
