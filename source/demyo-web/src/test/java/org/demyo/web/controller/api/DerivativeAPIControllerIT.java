package org.demyo.web.controller.api;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

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
				.andExpect(jsonPath("$", hasSize(28))) //
				// Check first entry. Includes some checks for properties that shouldn't be mentioned
				.andExpect(jsonPath("$[0].id").value(205)) //
				.andExpect(jsonPath("$[0].album.id").value(1313)) //
				.andExpect(jsonPath("$[0].total").doesNotExist()) //
				.andExpect(jsonPath("$[0].artist").doesNotExist());
	}

	@Test
	public void saveExisting() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.put("/api/derivatives/205")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{"
						+ "\"id\":205,"
						+ "\"acquisitionDate\":\"2010-01-01\","
						+ "\"album\":{\"id\":1313},"
						+ "\"artist\":{\"id\":201},"
						+ "\"type\":{\"id\":2},"
						+ "\"colours\":5,"
						+ "\"source\":{\"id\":1},"
						+ "\"number\":176,"
						+ "\"total\":200,"
						+ "\"signed\":true,"
						+ "\"authorsCopy\":false,"
						+ "\"restrictedSale\":false,"
						+ "\"description\":\"<p>Foo</p>\","
						+ "\"height\":280,"
						+ "\"width\":205,"
						+ "\"images\":[{\"id\":10}],"
						+ "\"mainImage\":{\"id\":1328}," // Don't change this, it should have an impact
						+ "\"identifyingName\":\"Maître d'Armes (Le) - Sérigraphie Durango\","
						+ "\"baseNameForImages\":\"Maître d'Armes (Le) - Sérigraphie Durango\","
						+ "\"series\":{}"
						+ "}"))

				.andExpect(status().isOk())
				.andExpect(content().string("205"));

		// Now that we know the save was OK, check if the content was actually saved
		// We only check the data that was changed compared to the database
		mockMvc.perform(get("/api/derivatives/205"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(205))
				.andExpect(jsonPath("$.acquisitionDate").value("2010-01-01"))
				.andExpect(jsonPath("$.description").value("<p>Foo</p>"))
				.andExpect(jsonPath("$.images[0].id").value("10"))
				.andExpect(jsonPath("$.mainImage.id").value("10"));
	}

	@Test
	public void saveWithValidationErrors() throws Exception {
		// TODO
	}
}
