package org.demyo.web.controller.api;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the Author API.
 */
@DatabaseSetup(value = "/org/demyo/test/demyo-dbunit-standard.xml", type = DatabaseOperation.REFRESH)
class SeriesAPIControllerIT extends AbstractModelAPIIT {
	public SeriesAPIControllerIT() {
		super("/api/series/");
	}

	@Test
	void index() throws Exception {
		mockMvc.perform(get("/api/series/"))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(10)))
				.andExpect(jsonPath("$[1].id").value(312))
				.andExpect(jsonPath("$[1].identifyingName").value("Alvin"))
				.andExpect(jsonPath("$[1].comment").doesNotExist())
				.andExpect(jsonPath("$[1].location").doesNotExist());
	}

	@Test
	void view() throws Exception {
		mockMvc.perform(get("/api/series/172"))
				.andDo(MockMvcResultHandlers.print())
				.andDo(logResolvedException())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(172))
				.andExpect(jsonPath("$.name").value("Cixi de Troy"))
				.andExpect(jsonPath("$.universe.id").value(8))
				.andExpect(jsonPath("$.albumIds", hasSize(3)))
				.andExpect(jsonPath("$.albumIds", contains(800, 859, 949)))
				.andExpect(jsonPath("$.albums").doesNotExist())
				.andExpect(jsonPath("$.albumTaxons").doesNotExist())
				.andExpect(jsonPath("$.albumTags").doesNotExist())
				.andExpect(jsonPath("$.albumGenres").doesNotExist());
	}

	@Test
	void getAlbumsForSeries() throws Exception {
		mockMvc.perform(get("/api/series/172/albums"))
				.andExpect(status().isOk())
				.andDo(MockMvcResultHandlers.print())
				.andExpect(jsonPath("$", hasSize(3)))
				// Check first entry. Include some checks for properties that shouldn't be mentioned
				.andExpect(jsonPath("$[0].id").value(800))
				.andExpect(jsonPath("$[0].identifyingName").value("1 - Le Secret de Cixi - 1ère partie"))
				.andExpect(jsonPath("$[0].publisher").doesNotExist())
				.andExpect(jsonPath("$[0].artists").doesNotExist())
				// Check second entry. Only basic stuff
				.andExpect(jsonPath("$[1].id").value(859))
				.andExpect(jsonPath("$[1].identifyingName").value("2 - Le Secret de Cixi - 2ème Partie"))
				// Check last entry. Again basic stuff
				.andExpect(jsonPath("$[2].id").value(949))
				.andExpect(jsonPath("$[2].identifyingName").value("3 - Le Secret de Cixi - 3ème Partie"));
	}

	@Test
	void getAlbumsWithoutSeries() throws Exception {
		mockMvc.perform(get("/api/series/none/albums"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].id").value(2892))
				.andExpect(jsonPath("$[0].identifyingName").value("300"));
	}

	@Test
	void countDerivativesBySeries() throws Exception {
		mockMvc.perform(get("/api/series/107/derivatives/count"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").value(9));
	}

	@Test
	void saveExisting() throws Exception {
		mockMvc.perform(put("/api/series/312")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{"
						+ "\"id\":312,"
						+ "\"name\":\"Alvin 2\","
						+ "\"universe\": {}"
						+ "}"))

				.andExpect(status().isOk())
				.andExpect(content().string("312"));

		// Now that we know the save was OK, check if the content was actually saved
		// We only check the data that was changed compared to the database
		mockMvc.perform(get("/api/series/312"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(312))
				.andExpect(jsonPath("$.name").value("Alvin 2"));
	}
}
