package org.demyo.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.demyo.common.desktop.DesktopCallbacks;
import org.demyo.test.desktop.TestDesktopCallbacks;

@Configuration
public class TestWebConfig extends WebConfig {
	/**
	 * Returns a mocked instance of the callbacks.
	 */
	@Override
	@Bean
	public DesktopCallbacks desktopCallbacks() {
		return new TestDesktopCallbacks();
	}
}
