package org.demyo.model.config;

import org.demyo.common.config.SystemConfiguration;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests from {@link SystemConfiguration}.
 */
public class SystemConfigurationTest {
	@Test
	public void testLoadDefaultConfig() {
		SystemConfiguration config = SystemConfiguration.getInstance();
		Assert.assertEquals(8080, config.getHttpPort());
	}
}
