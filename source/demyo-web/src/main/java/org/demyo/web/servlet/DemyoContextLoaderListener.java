package org.demyo.web.servlet;

import jakarta.servlet.ServletContextEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoaderListener;

/**
 * Servlet context listener that rethrows Spring initialization errors to make the container Web Application context
 * fail.
 */
// TODO: #205: Probably not needed anymore
public class DemyoContextLoaderListener extends ContextLoaderListener {
	private static final Logger LOGGER = LoggerFactory.getLogger(DemyoContextLoaderListener.class);

	@Override
	public void contextInitialized(ServletContextEvent event) {
		try {
			super.contextInitialized(event);
		} catch (RuntimeException e) {
			LOGGER.error("Error during context load");
			throw e;
		}
	}

}
