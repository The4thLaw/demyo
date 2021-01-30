package org.demyo.web.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Java-based configuration for Spring Security.
 */
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	private static final Logger LOGGER = LoggerFactory.getLogger(WebSecurityConfig.class);

	public WebSecurityConfig() {
		LOGGER.info("Parsing configuration");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
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
				.and().frameOptions().sameOrigin().xssProtection().block(true);

		// Disable CSRF protection for now
		http.csrf().disable();

		LOGGER.debug("Configuring authorization");
		http.authorizeRequests().anyRequest().permitAll();
	}
}
