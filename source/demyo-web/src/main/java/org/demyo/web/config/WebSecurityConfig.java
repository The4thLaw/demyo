package org.demyo.web.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import org.demyo.web.servlet.StagemonitorInitializer;

/**
 * Java-based configuration for Spring Security.
 */
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	private static final Logger LOGGER = LoggerFactory.getLogger(WebSecurityConfig.class);

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		LOGGER.debug("Configuring Spring Security");

		boolean allowUnsafeEval = StagemonitorInitializer.isStagemonitorAvailable();
		if (allowUnsafeEval) {
			LOGGER.warn("Stagemonitor is available, the CSP will allow unsafe-eval for JavaScript");
		}

		// Using recommendations from https://content-security-policy.com/
		String csp = "default-src 'none'; font-src 'self'; ";
		// Blob is used by the FilePond image preview plugin
		csp += "connect-src 'self' blob:; ";
		// Data is used by Material Design for e.g. the check mark in checkboxes
		// Blob is used by the FilePond image preview plugin
		csp += "img-src 'self' data: blob:; ";
		// unsafe-inline is required, notably because of LESS on the server side
		// Blob is used by the FilePond image preview plugin
		csp += "script-src 'self' blob: 'unsafe-inline' "//
				+ (allowUnsafeEval ? "'unsafe-eval'" : "")//
				+ "; style-src 'self' 'unsafe-inline';";

		LOGGER.debug("Configuring headers");
		http.headers()
				// Don't use the defaults
				.defaultsDisabled()
				// Don't use their cache control directives. We don't have
				// really sensitive control yet
				.contentTypeOptions()
				// Don't care for HSTS: It's not supported by Demyo yet anyway
				// Do the following even though we will implement CSP afterwards
				.and().frameOptions().sameOrigin().xssProtection().block(true).and().contentSecurityPolicy(csp);

		// Disable CSRF protection for now
		http.csrf().disable();

		LOGGER.debug("Configuring authorization");
		http.authorizeRequests().anyRequest().permitAll();
	}
}
