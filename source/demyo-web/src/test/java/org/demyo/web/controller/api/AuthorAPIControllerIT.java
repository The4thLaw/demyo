package org.demyo.web.controller.api;

import org.junit.jupiter.api.Test;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

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
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(7)))
				// Check first entry. Include some checks for properties that shouldn't be mentioned
				.andExpect(jsonPath("$[3].id").value(717))
				.andExpect(jsonPath("$[3].name").value("Montlló Ribó"))
				.andExpect(jsonPath("$[3].firstName").value("Miquel"))
				.andExpect(jsonPath("$[3].nickname").doesNotExist())
				// Check second entry. Only basic stuff
				.andExpect(jsonPath("$[0].id").value(52))
				.andExpect(jsonPath("$[0].name").value("Buchet"))
				.andExpect(jsonPath("$[0].firstName").value("Philippe"));
	}

	@Test
	void view() throws Exception {
		mockMvc.perform(get("/api/authors/610"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(610))
				.andExpect(jsonPath("$.name").value("Chan"))
				.andExpect(jsonPath("$.firstName").value("Pierre-Mony"))
				.andExpect(jsonPath("$.identifyingName").value("Pierre-Mony Chan"))
				.andExpect(jsonPath("$.website").value("http://pmchan.fr.nf/"));
	}

	@Test
	void getAuthorAlbums() throws Exception {
		mockMvc.perform(get("/api/authors/610/albums"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.asArtist", hasSize(1)))
				.andExpect(jsonPath("$.asArtist[0]").value(1093))
				.andExpect(jsonPath("$.asColorist", hasSize(1)))
				.andExpect(jsonPath("$.asColorist[0]").value(1093))
				.andExpect(jsonPath("$.asWriter").doesNotExist())
				.andExpect(jsonPath("$.asInker").doesNotExist())
				.andExpect(jsonPath("$.asTranslator").doesNotExist())
				.andExpect(jsonPath("$.asCoverArtist").doesNotExist())
				.andExpect(jsonPath("$.albums", hasSize(1)))
				.andExpect(jsonPath("$.albums[0].id").value(1093))
				.andExpect(jsonPath("$.albums[0].title").value("Esprit d'Équipe"));
	}

	@Test
	void countDerivativesByArtist() throws Exception {
		mockMvc.perform(get("/api/authors/52/derivatives/count"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").value(27));
	}
}
