package org.demyo.web.controller.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;

/**
 * Integration tests for the Derivative API.
 */
public class TranslationAPIControllerIT extends AbstractAPIIT {
	@Test
	public void getTranslations() throws Exception {
		mockMvc.perform(get("/api/translations/en")) //
				.andExpect(status().isOk()) //
				.andExpect(jsonPath("$['title.home']").value("Home"));

		mockMvc.perform(get("/api/translations/fr")) //
				.andExpect(status().isOk()) //
				.andExpect(jsonPath("$['title.home']").value("Accueil"));
	}
}
