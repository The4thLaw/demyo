package org.demyo.web.controller.api;

import org.junit.jupiter.api.Test;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
				.andExpect(jsonPath("$[0].id").value(7))
				.andExpect(jsonPath("$[0].identifyingName").value("Cello (Sérigraphie ou Offset)"));
	}

	@Test
	void countDerivativesByType() throws Exception {
		mockMvc.perform(get("/api/derivativeTypes/7/derivatives/count"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").value(1));
	}
}
