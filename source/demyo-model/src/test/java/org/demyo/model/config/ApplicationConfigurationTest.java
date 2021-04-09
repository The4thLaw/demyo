package org.demyo.model.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * Tests for the {@link ApplicationConfiguration} class.
 */
class ApplicationConfigurationTest {
	private static final String SAMPLE_CURRENCY = "EUR";

	/**
	 * Tests that the configuration can be loaded from a simple hard-coded set of properties.
	 */
	@Test
	void testInitialisationFromStrings() {
		Map<String, String> configStrings = new HashMap<>();
		configStrings.put(ApplicationConfiguration.CONFIG_KEY_LANGUAGE, "en");
		configStrings.put("paging.textPageSize", "50");
		configStrings.put("paging.imagePageSize", "25");
		ApplicationConfiguration config = new ApplicationConfiguration(configStrings);
		assertThat(config.getPageSizeForText()).isEqualTo(50);
	}

	/**
	 * Tests {@link ApplicationConfiguration#asMap()}.
	 */
	@Test
	void asMap() {
		ApplicationConfiguration config = ApplicationConfiguration.getDefaultConfiguration();

		config.setCurrency(SAMPLE_CURRENCY);

		Map<String, String> map = config.asMap();
		assertThat(map).containsEntry("currency", SAMPLE_CURRENCY);
	}
}
