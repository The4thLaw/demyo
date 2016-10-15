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
		configStrings.put("paging.albumPageSize", "10");
		configStrings.put("thumbnail.width", "200");
		configStrings.put("thumbnail.height", "300");
		configStrings.put("header.quickLinks", "[ { \"urlFromRoot\": \"albums/\","
				+ " \"iconSpec\": \"speech_bubble\", \"label\": \"menu.albums.browse\" } ]");
		ApplicationConfiguration config = new ApplicationConfiguration(configStrings);
		assertThat(config.getHeaderLinks()).hasSize(1);

	}
}
