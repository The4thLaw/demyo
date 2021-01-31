package org.demyo.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.demyo.test.desktop.TestDesktopCallbacks;

@Configuration
public class TestWebConfig extends WebConfig {
	/**
	 * Returns a mocked instance of the callbacks.
	 */
	@Override
	@Bean("desktopCallbacks")
	public Object desktopCallbacks() {
		return new TestDesktopCallbacks();
	}
}
