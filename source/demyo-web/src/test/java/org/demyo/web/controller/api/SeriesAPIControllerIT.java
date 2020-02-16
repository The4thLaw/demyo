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
 * Integration tests for the Author API.
 */
@DatabaseSetup(value = "/org/demyo/test/demyo-dbunit-standard.xml", type = DatabaseOperation.REFRESH)
public class SeriesAPIControllerIT extends AbstractModelAPIIT {
	public SeriesAPIControllerIT() {
		super("/api/series/");
	}

	@Test
	public void getAlbumsForSeries() throws Exception {
		mockMvc.perform(get("/api/series/2/albums"))
				.andExpect(status().isOk())
				// Sample empty series
				.andExpect(jsonPath("$", hasSize(0)));

		mockMvc.perform(get("/api/series/99/albums"))
				.andExpect(status().isOk())
				.andDo(MockMvcResultHandlers.print())
				.andExpect(jsonPath("$", hasSize(24)))
				// Check first entry. Include some checks for properties that shouldn't be mentioned
				.andExpect(jsonPath("$[0].id").value(444))
				.andExpect(jsonPath("$[0].identifyingName").value("HS - Le Collectionneur"))
				.andExpect(jsonPath("$[0].publisher").doesNotExist())
				.andExpect(jsonPath("$[0].artists").doesNotExist())
				// Check second entry. Only basic stuff
				.andExpect(jsonPath("$[1].id").value(737))
				.andExpect(jsonPath("$[1].identifyingName").value("HS - BlockBuster"))
				// Check last entry. Again basic stuff
				.andExpect(jsonPath("$[23].id").value(1370))
				.andExpect(jsonPath("$[23].identifyingName").value("19 - Temps mort"));
	}

	@Test
	public void getAlbumsWithoutSeries() throws Exception {
		mockMvc.perform(get("/api/series/none/albums"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].id").value(1313))
				.andExpect(jsonPath("$[0].identifyingName").value("Maître d'Armes (Le)"));
	}
}
