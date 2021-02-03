package org.demyo.web.controller.api;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

/**
 * Integration tests for the Reader API.
 */
@DatabaseSetup(value = "/org/demyo/test/demyo-dbunit-standard.xml", type = DatabaseOperation.REFRESH)
public class ReaderAPIControllerIT extends AbstractModelAPIIT {
	public ReaderAPIControllerIT() {
		super("/api/readers/");
	}

	@Test
	public void autoSelect() throws Exception {
		mockMvc.perform(get("/api/readers/autoSelect"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name").value("Xavier"))
				.andExpect(jsonPath("$.colour").value("#304ffe"))
				.andExpect(jsonPath("$.identifyingName").value("Xavier"))
				.andExpect(jsonPath("$.configurationEntries", hasSize(greaterThan(2))));
	}

	@Test
	public void getLists() throws Exception {
		mockMvc.perform(get("/api/readers/1/lists"))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.favouriteSeries", hasSize(1)))
				.andExpect(jsonPath("$.favouriteSeries", Matchers.hasItems(99)))
				.andExpect(jsonPath("$.favouriteAlbums", hasSize(1)))
				.andExpect(jsonPath("$.favouriteAlbums", Matchers.hasItems(1313)))
				.andExpect(jsonPath("$.readingList", hasSize(1)))
				.andExpect(jsonPath("$.readingList", Matchers.hasItems(1459)));
	}
}
