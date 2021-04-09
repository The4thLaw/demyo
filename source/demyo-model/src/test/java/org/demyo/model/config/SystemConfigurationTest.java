package org.demyo.model.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import org.demyo.common.config.SystemConfiguration;

/**
 * Tests from {@link SystemConfiguration}.
 */
class SystemConfigurationTest {
	private static final int EXPECTED_DEFAULT_PORT = 8080;

	/**
	 * Tests the loading of the default system configuration.
	 */
	@Test
	void testLoadDefaultConfig() {
		SystemConfiguration config = SystemConfiguration.getInstance();
		assertThat(config.getHttpPort()).isEqualTo(EXPECTED_DEFAULT_PORT);
		assertThat(config.getContextRoot()).isEqualTo("/");
	}
}
