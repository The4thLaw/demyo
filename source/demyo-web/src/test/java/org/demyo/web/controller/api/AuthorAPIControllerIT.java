package org.demyo.web.controller.api;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import org.demyo.test.AbstractPersistenceTest;

@ContextConfiguration("file:src/main/webapp/WEB-INF/spring/demyo-context.xml")
@WebAppConfiguration
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
@DatabaseSetup(value = "/org/demyo/test/demyo-dbunit-standard.xml", type = DatabaseOperation.REFRESH)
// TODO [boot]: use @WebMvcTest when migrating to Spring Boot
// TODO: extract abstract test class. Some tests could be automated completely (404 errors, for example) ?
// TODO: build a good base dbunit xml (and adapt the other tests accordingly)
public class AuthorAPIControllerIT extends AbstractPersistenceTest {
	@Autowired
	private WebApplicationContext wac;
	@Autowired
	private CacheManager cacheManager;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		// MockMvc is not guaranteed to be reuseable between tests so we create it as an instance field in a @Before
		// Moreover, we use the more costly webAppContextSetup rather than standaloneSetup to make sure that everything
		// works together (advices, etc)
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}

	/**
	 * Clears all caches.
	 */
	@Before
	public void clearCaches() {
		// We must clear all caches for each test case, else the reloads by
		// DBUnit break the tests
		// TODO: test if this is still true
		for (String cacheName : cacheManager.getCacheNames()) {
			cacheManager.getCache(cacheName).clear();
		}
	}

	@Test
	public void index() throws Exception {
		// TODO: extract to other method
		mockMvc.perform(MockMvcRequestBuilders.get("/api/authors/421337")) //
				.andExpect(MockMvcResultMatchers.status().isNotFound());

		mockMvc.perform(MockMvcRequestBuilders.get("/api/authors/")) //
				.andExpect(MockMvcResultMatchers.status().isOk()) //
				// .andExpect(MockMvcResultMatchers.content().string("")) //
				.andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)));
	}
}
