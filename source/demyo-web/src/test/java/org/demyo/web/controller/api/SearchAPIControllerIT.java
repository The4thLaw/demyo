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
	@Test
	public void quicksearch() throws Exception {
		mockMvc.perform(get("/api/search/quick?q=sill"))
				.andExpect(status().isOk())
				.andDo(MockMvcResultHandlers.print())
				.andExpect(jsonPath("$.albums").doesNotExist())
				.andExpect(jsonPath("$.series", hasSize(2)))
				.andExpect(jsonPath("$.series[0].identifyingName").value("Sillage"))
		// .andExpect(jsonPath("$.id").value(1))
		// .andExpect(jsonPath("$.configurationEntries", hasSize(greaterThan(2))))
		;

		mockMvc.perform(get("/api/search/quick?q=feu"))
				.andExpect(status().isOk())
				.andDo(MockMvcResultHandlers.print());
	}
}
