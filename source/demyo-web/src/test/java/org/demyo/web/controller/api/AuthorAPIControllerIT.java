package org.demyo.web.controller.api;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the Author API.
 */
@DatabaseSetup(value = "/org/demyo/test/demyo-dbunit-standard.xml", type = DatabaseOperation.REFRESH)
class AuthorAPIControllerIT extends AbstractModelAPIIT {
	public AuthorAPIControllerIT() {
		super("/api/authors/");
	}

	@Test
	void index() throws Exception {
		mockMvc.perform(get("/api/authors/"))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(31)))
				// Check a specific entry. Include some checks for properties that shouldn't be mentioned
				.andExpect(jsonPath("$[3].id").value(216))
				.andExpect(jsonPath("$[3].name").value("Besson"))
				.andExpect(jsonPath("$[3].firstName").value("Frédéric"))
				.andExpect(jsonPath("$[3].nickname").doesNotExist())
				// Check the first entry. Only basic stuff
				.andExpect(jsonPath("$[0].id").value(665))
				.andExpect(jsonPath("$[0].name").value("Alwett"))
				.andExpect(jsonPath("$[0].firstName").value("Audrey"));
	}

	@Test
	void view() throws Exception {
		mockMvc.perform(get("/api/authors/10"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(10))
				.andExpect(jsonPath("$.name").value("Vatine"))
				.andExpect(jsonPath("$.firstName").value("Olivier"))
				.andExpect(jsonPath("$.identifyingName").value("Olivier Vatine"))
				.andExpect(jsonPath("$.biography").value("<p>Sample HTML description</p>"));
	}

	@Test
	void getAuthorAlbums() throws Exception {
		mockMvc.perform(get("/api/authors/10/albums"))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.asArtist", hasSize(3)))
				.andExpect(jsonPath("$.asArtist", containsInAnyOrder(800, 859, 949)))
				.andExpect(jsonPath("$.asColorist", hasSize(1)))
				.andExpect(jsonPath("$.asColorist[0]").value(800))
				.andExpect(jsonPath("$.asWriter").doesNotExist())
				.andExpect(jsonPath("$.asInker").doesNotExist())
				.andExpect(jsonPath("$.asTranslator").doesNotExist())
				.andExpect(jsonPath("$.asCoverArtist").doesNotExist())
				.andExpect(jsonPath("$.albums", hasSize(3)))
				.andExpect(jsonPath("$.albums[0].id").value(800))
				.andExpect(jsonPath("$.albums[0].title").value("Le Secret de Cixi - 1ère partie"));
	}

	@Test
	void countDerivativesByArtist() throws Exception {
		mockMvc.perform(get("/api/authors/119/derivatives/count"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").value(18));
	}
}
