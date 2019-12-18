package org.demyo.web.controller.api;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Base class for API integration tests that work on standard Demyo models.
 */
public abstract class AbstractModelAPIIT extends AbstractAPIIT {
	private final String apiRoot;

	public AbstractModelAPIIT(String apiRoot) {
		this.apiRoot = apiRoot;
	}

	/** Generic test for index methods that ensures at least one entity is returned. */
	@Test
	public void genTestIndex() throws Exception {
		mockMvc.perform(get(apiRoot)) //
				.andExpect(status().isOk()) //
				.andExpect(jsonPath("$", hasSize(Matchers.greaterThan(0))));
	}

	/**
	 * Generic test for view methods that ensures 404 errors are returned.
	 */
	@Test
	public void genTestMissingEntityView() throws Exception {
		mockMvc.perform(get(apiRoot + "421337")) //
				.andExpect(status().isNotFound());
	}
}
