package org.demyo.model.config;

import org.junit.Assert;
import org.junit.Test;

import org.demyo.common.config.SystemConfiguration;

/**
 * Tests from {@link SystemConfiguration}.
 */
public class SystemConfigurationTest {
	private static final int EXPECTED_DEFAULT_PORT = 8080;

	/**
	 * Tests the loading of the default system configuration.
	 */
	@Test
	public void testLoadDefaultConfig() {
		SystemConfiguration config = SystemConfiguration.getInstance();
		Assert.assertEquals(EXPECTED_DEFAULT_PORT, config.getHttpPort());
		Assert.assertEquals("/", config.getContextRoot());
	}
}
