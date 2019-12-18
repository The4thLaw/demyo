package org.demyo.web.controller.api;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

/**
 * Integration tests for the Derivative API.
 */
@DatabaseSetup(value = "/org/demyo/test/demyo-dbunit-standard.xml", type = DatabaseOperation.REFRESH)
public class DerivativeAPIControllerIT extends AbstractModelAPIIT {
	public DerivativeAPIControllerIT() {
		super("/api/derivatives/");
	}

	@Test
	public void index() throws Exception {
		mockMvc.perform(get("/api/derivatives/")) //
				.andExpect(status().isOk()) //
				.andExpect(jsonPath("$", hasSize(3))) //
				// Check first entry. Includes some checks for properties that shouldn't be mentioned
				.andExpect(jsonPath("$[0].id").value(3)) //
				.andExpect(jsonPath("$[0].album.id").value(2)) //
				.andExpect(jsonPath("$[0].total").doesNotExist()) //
				.andExpect(jsonPath("$[0].artist").doesNotExist());
	}
}
