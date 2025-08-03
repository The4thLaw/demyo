package org.demyo.web.controller.api;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the Reader API.
 */
@DatabaseSetup(value = "/org/demyo/test/demyo-dbunit-standard.xml", type = DatabaseOperation.REFRESH)
class ReaderAPIControllerIT extends AbstractModelAPIIT {
	public ReaderAPIControllerIT() {
		super("/api/readers/");
	}

	@Test
	void autoSelect() throws Exception {
		mockMvc.perform(get("/api/readers/autoSelect"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name").value("My Reader"))
				.andExpect(jsonPath("$.colour").value("#304ffe"))
				.andExpect(jsonPath("$.identifyingName").value("My Reader"))
				.andExpect(jsonPath("$.configurationEntries", hasSize(greaterThan(2))));
	}

	@Test
	void getLists() throws Exception {
		mockMvc.perform(get("/api/readers/1/lists"))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.favouriteSeries", hasSize(3)))
				.andExpect(jsonPath("$.favouriteSeries", Matchers.containsInAnyOrder(169, 296, 312)))
				.andExpect(jsonPath("$.favouriteAlbums", hasSize(1)))
				.andExpect(jsonPath("$.favouriteAlbums", Matchers.hasItem(1362)))
				.andExpect(jsonPath("$.readingList", hasSize(15)))
				.andExpect(jsonPath("$.readingList", Matchers.containsInAnyOrder(1515, 1516, 1517, 1518, 1519, 1520,
						1521, 1552, 1561, 1562, 1563, 1770, 1992, 2030, 2892)));
	}
}
