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

		// Using recommendations from https://content-security-policy.com/
		String csp = "default-src 'none'; connect-src 'self'; font-src 'self'; ";
		// Data is used by Filepond, notably (in the CSS, though. Not sure whether that would be an issue)
		csp += "img-src 'self' data:; ";

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

		// TODO: figure out why this security config is not set on / (and thus is not working on any page
		// since we only have /). Perhaps Spring-Security is not called at all on the root?
		// The headers are set by
		// org.springframework.security.web.header.writers.ContentSecurityPolicyHeaderWriter.writeHeaders(HttpServletRequest,
		// HttpServletResponse)
		// but something seems to be removing them
		// It could be because JSP's are handled by Jetty afterwards and Jetty removes the headers, so check after
		// updating jetty, Spring and Spring security. Meanwhile we should set the headers in HomeController

		// Disable CSRF protection for now
		http.csrf().disable();

		LOGGER.debug("Configuring authorization");
		http.authorizeRequests().anyRequest().permitAll();
	}
}
