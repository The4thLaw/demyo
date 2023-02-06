package org.demyo.web.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;

/**
 * Java-based configuration for Spring Security.
 */
@EnableWebSecurity
public class WebSecurityConfig {
	private static final Logger LOGGER = LoggerFactory.getLogger(WebSecurityConfig.class);

	public WebSecurityConfig() {
		LOGGER.info("Parsing configuration");
	}

	@Bean
	protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		LOGGER.debug("Configuring Spring Security");

		LOGGER.debug("Configuring headers");
		http.headers()
				// Don't use the defaults
				.defaultsDisabled()
				// Add our custom CSP
				.addHeaderWriter(new NoncedCSPHeaderWriter())
				// Don't use their cache control directives. We don't have really sensitive control yet
				.contentTypeOptions()
				// Don't care for HSTS: It's not supported by Demyo yet anyway
				// Don't use the Spring Security CSP: it doesn't support nonces
				.and().frameOptions().sameOrigin().xssProtection().headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK);

		// Disable CSRF protection for now
		http.csrf().disable();

		LOGGER.debug("Configuring authorization");
		http.authorizeHttpRequests().anyRequest().permitAll();

		return http.build();
	}
}
