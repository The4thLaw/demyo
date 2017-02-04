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

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		LOGGER.debug("Configuring Spring Security");

		LOGGER.debug("Configuring headers");
		http.headers()
				// Don't use the defaults
				.defaultsDisabled()
				// Don't use their cache control directives. We don't have really sensitive control yet
				.contentTypeOptions()
				// Don't care for HSTS: It's not supported by Demyo yet anyway
				// Do the following even though we will implement CSP afterwards
				.and()
				.frameOptions()
				.sameOrigin()
				.xssProtection()
				.block(true)
				// Using recommendations from https://content-security-policy.com/
				// unsafe-inline is required, probably because of LESS on the server side
				.and()
				.contentSecurityPolicy(
						"default-src 'none'; script-src 'self' 'unsafe-inline'; connect-src 'self'; img-src 'self'; "
								+ "style-src 'self' 'unsafe-inline'; font-src 'self';");

		// Disable CSRF protection for now
		http.csrf().disable();

		LOGGER.debug("Configuring authorization");
		http.authorizeRequests().anyRequest().permitAll();
	}
}
