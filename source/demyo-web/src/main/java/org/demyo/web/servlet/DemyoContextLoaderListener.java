package org.demyo.web.servlet;

import javax.servlet.ServletContextEvent;

import org.springframework.web.context.ContextLoaderListener;

/**
 * Servlet context listener that rethrows Spring initialization errors to make the container Web Application
 * context fail.
 */
public class DemyoContextLoaderListener extends ContextLoaderListener {

	@Override
	public void contextInitialized(ServletContextEvent event) {
		try {
			super.contextInitialized(event);
		} catch (RuntimeException e) {
			throw e;
		}
	}

}
