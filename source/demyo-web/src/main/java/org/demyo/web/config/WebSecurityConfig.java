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

		return http
				// Headers
				.headers(headers -> headers
						// Don't use the defaults
						.defaultsDisabled()
						// Add our custom CSP instead of using .contentSecurityPolicy: it's easier to work with nonces
						.addHeaderWriter(new NoncedCSPHeaderWriter())
						// Keep the content-type options
						// Don't use their cache control directives. We don't have really sensitive control yet
						.cacheControl(cc -> cc.disable())
						// .contentTypeOptions().and()
						.frameOptions(fo -> fo
								.sameOrigin()
								.xssProtection(xss -> xss
										.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK)))
						// Don't care for HSTS: It's not supported by Demyo yet anyway
						.httpStrictTransportSecurity(hsts -> hsts.disable()))

				// Disable CSRF protection for now
				.csrf(csrf -> csrf.disable())

				// Allow all requests
				.authorizeHttpRequests(auth -> auth.anyRequest().permitAll())

				// Done
				.build();
	}
}
