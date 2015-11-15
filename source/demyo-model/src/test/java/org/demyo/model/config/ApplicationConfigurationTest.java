package org.demyo.model.config;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Assert;
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
		Configuration configStrings = new BaseConfiguration();
		configStrings.setProperty("language", "en");
		configStrings.setProperty("paging.textPageSize", 50);
		configStrings.setProperty("paging.imagePageSize", 25);
		configStrings.setProperty("paging.albumPageSize", 10);
		configStrings.setProperty("thumbnail.width", 200);
		configStrings.setProperty("thumbnail.height", 300);
		configStrings
				.setProperty("header.quickLinks",
						"[ { \"urlFromRoot\": \"albums/\"\\, \"iconSpec\": \"speech_bubble\"\\, \"label\": \"menu.albums.browse\" } ]");
		ApplicationConfiguration config = new ApplicationConfiguration(configStrings);
		Assert.assertEquals(1, config.getHeaderLinks().size());
	}

	/**
	 * Tests that the default configuration file can be loaded.
	 * 
	 * @throws ConfigurationException In case of error while loading the default configuration.
	 */
	@Test
	public void testInitialisationFromDefaultConfig() throws ConfigurationException {
		ApplicationConfiguration config = new ApplicationConfiguration(new PropertiesConfiguration(
				ApplicationConfiguration.class.getResource("/org/demyo/model/config/demyo-config.properties")));
		Assert.assertEquals(2, config.getHeaderLinks().size());
	}
}
