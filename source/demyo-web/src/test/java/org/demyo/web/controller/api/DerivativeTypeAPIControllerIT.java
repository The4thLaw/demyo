package org.demyo.web.controller.api;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

/**
 * Integration tests for the DerivativeType API.
 */
@DatabaseSetup(value = "/org/demyo/test/demyo-dbunit-standard.xml", type = DatabaseOperation.REFRESH)
class DerivativeTypeAPIControllerIT extends AbstractModelAPIIT {
	public DerivativeTypeAPIControllerIT() {
		super("/api/derivativeTypes/");
	}

	@Test
	void index() throws Exception {
		mockMvc.perform(get("/api/derivativeTypes/"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(5)))
				// Check first entry. Include some checks for properties that shouldn't be mentioned
				.andExpect(jsonPath("$[0].id").value(5))
				.andExpect(jsonPath("$[0].identifyingName").value("Carnet de croquis"));
	}

	@Test
	void countDerivativesByType() throws Exception {
		mockMvc.perform(get("/api/derivativeTypes/5/derivatives/count"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").value(1));
	}
}
