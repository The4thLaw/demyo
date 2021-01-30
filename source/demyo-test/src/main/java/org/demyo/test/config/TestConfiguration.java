package org.demyo.test.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.SimpleThreadScope;

/**
 * Standard test configuration for Demyo.
 */
@Configuration
public class TestConfiguration {
	// Thanks to https://stackoverflow.com/a/8803166/109813
	@Bean
	public CustomScopeConfigurer customScope() {
		CustomScopeConfigurer scopeConf = new CustomScopeConfigurer();
		Map<String, Object> scopes = new HashMap<>();
		scopes.put("session", new SimpleThreadScope());
		scopes.put("request", new SimpleThreadScope());
		scopeConf.setScopes(scopes);
		return scopeConf;
	}
}
