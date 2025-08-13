package org.demyo.web.controller.api;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.github.springtestdbunit.DbUnitTestExecutionListener;

import org.demyo.test.AbstractPersistenceTest;
import org.demyo.web.config.TestWebConfig;

@ContextConfiguration(classes = TestWebConfig.class)
@WebAppConfiguration
@TestExecutionListeners(
{ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
public abstract class AbstractAPIIT extends AbstractPersistenceTest {

	@Autowired
	private WebApplicationContext wac;
	@Autowired
	private CacheManager cacheManager;
	protected MockMvc mockMvc;

	@BeforeEach
	public void setup() {
		// MockMvc is not guaranteed to be reuseable between tests so we create it as an instance field in a @Before
		// Moreover, we use the more costly webAppContextSetup rather than standaloneSetup to make sure that everything
		// works together (advices, etc)
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}

	/**
	 * Clears all caches.
	 */
	@BeforeEach
	public void clearCaches() {
		// We must clear all caches for each test case, else the reloads by
		// DBUnit break the tests
		for (String cacheName : cacheManager.getCacheNames()) {
			cacheManager.getCache(cacheName).clear();
		}
	}

	/**
	 * Gets a result handler that prints more details about the resolved exception if it exists.
	 * @return the handler
	 */
	protected ResultHandler printResolvedException() {
		return result -> {
			Exception res = result.getResolvedException();
			if (res != null) {
				System.out.printf("MockHttpServletRequest:%n\tResolved Exception:%n\t\tType = %s%n\t\tMessage = %s%n",
					res.getClass().getName(), res.getMessage());
					res.printStackTrace();
			}
		};
	}
}
