package org.demyo.web.controller.api;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the Derivative API.
 */
@DatabaseSetup(value = "/org/demyo/test/demyo-dbunit-standard.xml", type = DatabaseOperation.REFRESH)
class DerivativeAPIControllerIT extends AbstractModelAPIIT {
	public DerivativeAPIControllerIT() {
		super("/api/derivatives/");
	}

	@Test
	void index() throws Exception {
		mockMvc.perform(get("/api/derivatives/")) //
				.andExpect(status().isOk()) //
				.andDo(MockMvcResultHandlers.print()) //
				.andExpect(jsonPath("$", hasSize(36))) //
				// Check first entry. Includes some checks for properties that shouldn't be mentioned
				.andExpect(jsonPath("$[0].id").value(491)) //
				.andExpect(jsonPath("$[0].album.id").value(1374)) //
				.andExpect(jsonPath("$[0].total").doesNotExist()) //
				.andExpect(jsonPath("$[0].artist").doesNotExist());
	}

	@Test
	void indexWithFilter() throws Exception {
		mockMvc.perform(post("/api/derivatives/index/filtered")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{"
						+ "\"type\": 3"
						+ "}")) //
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].id").value(113))
				.andExpect(jsonPath("$[0].series.identifyingName").value("Trolls de Troy"))
				.andExpect(jsonPath("$[0].album.id").value(653))
				.andExpect(jsonPath("$[0].total").doesNotExist())
				.andExpect(jsonPath("$[0].artist").doesNotExist());
	}

	@Test
	void saveExisting() throws Exception {
		mockMvc.perform(put("/api/derivatives/113")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{"
						+ "\"id\":113,"
						+ "\"acquisitionDate\":\"2010-01-01\","
						+ "\"album\":{\"id\":653},"
						+ "\"artist\":{\"id\":193},"
						+ "\"type\":{\"id\":3},"
						+ "\"colours\":42,"
						+ "\"source\":{\"id\":16},"
						+ "\"number\":0,"
						+ "\"total\":7000,"
						+ "\"signed\":false,"
						+ "\"authorsCopy\":false,"
						+ "\"restrictedSale\":false,"
						+ "\"description\":\"<p>Foo</p>\","
						+ "\"height\":79,"
						+ "\"width\":87,"
						+ "\"images\":[{\"id\":291}],"
						+ "\"mainImage\":{\"id\":1328}," // Don't change this, it should have an impact
						+ "\"series\":{\"id\":107}"
						+ "}"))

				.andExpect(status().isOk())
				.andExpect(content().string("113"));

		// Now that we know the save was OK, check if the content was actually saved
		// We only check the data that was changed compared to the database
		mockMvc.perform(get("/api/derivatives/113"))
				.andDo(logResolvedException())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(113))
				.andExpect(jsonPath("$.acquisitionDate").value("2010-01-01"))
				.andExpect(jsonPath("$.colours").value("42"))
				.andExpect(jsonPath("$.description").value("<p>Foo</p>"))
				.andExpect(jsonPath("$.images[0].id").value("291"))
				.andExpect(jsonPath("$.mainImage.id").value("291"));
	}

	@Test
	void saveWithValidationErrors() throws Exception {
		// Save a new entity with validation errors
		mockMvc.perform(post("/api/derivatives/")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content("{}"))
				.andExpect(status().isBadRequest())
				.andExpect(content().string(""));

		// Save an existing entity with validation errors
		mockMvc.perform(put("/api/derivatives/205")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content("{"
						+ "\"id\":205"
						+ "}"))
				.andExpect(status().isBadRequest())
				.andExpect(content().string(""));
	}
}
