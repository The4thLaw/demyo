package org.demyo.web.controller.api;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

/**
 * Integration tests for the Author API.
 */
@DatabaseSetup(value = "/org/demyo/test/demyo-dbunit-standard.xml", type = DatabaseOperation.REFRESH)
public class SeriesAPIControllerIT extends AbstractModelAPIIT {
	public SeriesAPIControllerIT() {
		super("/api/series/");
	}

	@Test
	public void index() throws Exception {
		mockMvc.perform(get("/api/series/"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(3)))
				.andExpect(jsonPath("$[1].id").value(99))
				.andExpect(jsonPath("$[1].identifyingName").value("Sillage"))
				.andExpect(jsonPath("$[1].name").doesNotExist())
				.andExpect(jsonPath("$[1].location").doesNotExist());
	}

	@Test
	public void view() throws Exception {
		mockMvc.perform(get("/api/series/99"))
				.andExpect(status().isOk())
				.andDo(MockMvcResultHandlers.print())
				.andExpect(jsonPath("$.id").value(99))
				.andExpect(jsonPath("$.name").value("Sillage"))
				.andExpect(jsonPath("$.website").value("http://www.poukram.org/"))
				.andExpect(jsonPath("$.relatedSeries", hasSize(1)))
				.andExpect(jsonPath("$.relatedSeries[0].id").value(232))
				.andExpect(jsonPath("$.albumIds", hasSize(24)))
				.andExpect(jsonPath("$.albumIds",
						contains(444, 737, 764, 435, 436, 437, 438, 439, 440, 441, 442, 443, 567, 669, 746, 848, 945,
								946, 1000, 1086, 1229, 1459, 1186, 1370)))
				.andExpect(jsonPath("$.albums").doesNotExist())
				.andExpect(jsonPath("$.albumTags").doesNotExist());
	}

	@Test
	public void getAlbumsForSeries() throws Exception {
		mockMvc.perform(get("/api/series/2/albums"))
				.andExpect(status().isOk())
				// Sample empty series
				.andExpect(jsonPath("$", hasSize(0)));

		mockMvc.perform(get("/api/series/99/albums"))
				.andExpect(status().isOk())
				.andDo(MockMvcResultHandlers.print())
				.andExpect(jsonPath("$", hasSize(24)))
				// Check first entry. Include some checks for properties that shouldn't be mentioned
				.andExpect(jsonPath("$[0].id").value(444))
				.andExpect(jsonPath("$[0].identifyingName").value("HS - Le Collectionneur"))
				.andExpect(jsonPath("$[0].publisher").doesNotExist())
				.andExpect(jsonPath("$[0].artists").doesNotExist())
				// Check second entry. Only basic stuff
				.andExpect(jsonPath("$[1].id").value(737))
				.andExpect(jsonPath("$[1].identifyingName").value("HS - BlockBuster"))
				// Check last entry. Again basic stuff
				.andExpect(jsonPath("$[23].id").value(1370))
				.andExpect(jsonPath("$[23].identifyingName").value("19 - Temps mort"));
	}

	@Test
	public void getAlbumsWithoutSeries() throws Exception {
		mockMvc.perform(get("/api/series/none/albums"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].id").value(1313))
				.andExpect(jsonPath("$[0].identifyingName").value("Maître d'Armes (Le)"));
	}

	@Test
	public void countDerivativesBySeries() throws Exception {
		mockMvc.perform(get("/api/series/99/derivatives/count"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").value(27));
	}

	@Test
	public void saveExisting() throws Exception {
		mockMvc.perform(put("/api/series/99")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{"
						+ "\"id\":99,"
						+ "\"name\":\"Sillage 2\","
						+ "\"relatedSeries\":[{\"id\":2}]"
						+ "}"))

				.andExpect(status().isOk())
				.andExpect(content().string("99"));

		// Now that we know the save was OK, check if the content was actually saved
		// We only check the data that was changed compared to the database
		mockMvc.perform(get("/api/series/99"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(99))
				.andExpect(jsonPath("$.name").value("Sillage 2"))
				.andExpect(jsonPath("$.relatedSeries", hasSize(1)))
				.andExpect(jsonPath("$.relatedSeries[0].id").value("2"));
	}
}
