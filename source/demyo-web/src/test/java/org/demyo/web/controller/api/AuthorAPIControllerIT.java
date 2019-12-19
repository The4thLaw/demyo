package org.demyo.web.controller.api;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

/**
 * Integration tests for the Author API.
 */
@DatabaseSetup(value = "/org/demyo/test/demyo-dbunit-standard.xml", type = DatabaseOperation.REFRESH)
public class AuthorAPIControllerIT extends AbstractModelAPIIT {
	public AuthorAPIControllerIT() {
		super("/api/authors/");
	}

	@Test
	public void index() throws Exception {
		mockMvc.perform(get("/api/authors/")) //
				.andExpect(status().isOk()) //
				.andExpect(jsonPath("$", hasSize(7))) //
				// Check first entry. Include some checks for properties that shouldn't be mentioned
				.andExpect(jsonPath("$[3].id").value(717)) //
				.andExpect(jsonPath("$[3].name").value("Montlló Ribó")) //
				.andExpect(jsonPath("$[3].firstName").value("Miquel")) //
				.andExpect(jsonPath("$[3].nickname").doesNotExist()) //
				.andExpect(jsonPath("$[3].albumsAsWriter").doesNotExist())
				// Check second entry. Only basic stuff
				.andExpect(jsonPath("$[0].id").value(52)) //
				.andExpect(jsonPath("$[0].name").value("Buchet")) //
				.andExpect(jsonPath("$[0].firstName").value("Philippe"));
	}
}
