package org.demyo.model.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * Tests for the {@link ApplicationConfiguration} class.
 */
public class ApplicationConfigurationTest {
	/**
	 * Tests that the configuration can be loaded from a simple hard-coded set of properties.
	 */
	@Test
	public void testInitialisationFromStrings() {
		Map<String, String> configStrings = new HashMap<>();
		configStrings.put("language", "en");
		configStrings.put("paging.textPageSize", "50");
		configStrings.put("paging.imagePageSize", "25");
		ApplicationConfiguration config = new ApplicationConfiguration(configStrings);
		assertThat(config.getPageSizeForText()).isEqualTo(50);
	}
}
