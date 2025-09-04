package org.demyo.web.controller.api;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the Author API.
 */
@DatabaseSetup(value = "/org/demyo/test/demyo-dbunit-standard.xml", type = DatabaseOperation.REFRESH)
class AlbumAPIControllerIT extends AbstractModelAPIIT {
	public AlbumAPIControllerIT() {
		super("/api/albums/");
	}

	@Test
	void index() throws Exception {
		mockMvc.perform(get("/api/albums/"))

				.andExpect(status().isOk())
				// 10 Series, 1 one shot
				.andExpect(jsonPath("$", hasSize(11)))
				// First series is actually an Album
				.andExpect(jsonPath("$[0].series").doesNotExist())
				.andExpect(jsonPath("$[0].album.title").value("300"))
				.andExpect(jsonPath("$[0].album.identifyingName").value("300"))
				// Second is a long series
				.andExpect(jsonPath("$[1].album").doesNotExist())
				.andExpect(jsonPath("$[1].series.identifyingName").value("Abélard"))
				.andExpect(jsonPath("$[1].albums", hasSize(2)))
				// Third is a short one
				.andExpect(jsonPath("$[2].album").doesNotExist())
				.andExpect(jsonPath("$[2].series.identifyingName").value("Alvin"))
				.andExpect(jsonPath("$[2].albums", hasSize(2)));
	}

	@Test
	void indexWithFilter() throws Exception {
		mockMvc.perform(post("/api/albums/index/filtered")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{"
						+ "\"binding\": 3"
						+ "}")) //
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				// Test the single match
				.andExpect(jsonPath("$[0].album").doesNotExist())
				.andExpect(jsonPath("$[0].series.identifyingName").value("Lanfeust de Troy"))
				.andExpect(jsonPath("$[0].albums", hasSize(1)))
				.andExpect(jsonPath("$[0].albums[0].title").value("Les Pétaures se Cachent pour Mourir"));
	}

	@Test
	void indexWithTaxonFilter() throws Exception {
		mockMvc.perform(post("/api/albums/index/filtered")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{"
						+ "\"taxon\": 24"
						+ "}")) //
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)))
				// Test the single match
				.andExpect(jsonPath("$[0].album").doesNotExist())
				.andExpect(jsonPath("$[0].series.identifyingName").value("Abélard"))
				.andExpect(jsonPath("$[0].albums", hasSize(2)))
				.andExpect(jsonPath("$[0].albums[1].title").value("Une brève histoire de poussière et de cendre"));
	}
}
