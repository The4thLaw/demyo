package org.demyo.web.servlet;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.WebApplicationInitializer;
import org.stagemonitor.web.WebPlugin;

/**
 * Specific component to ensure the StageMonitor plugin is initialised.
 */
@Component
public class StageMonitorInitializer implements WebApplicationInitializer {
	private static final Logger LOGGER = LoggerFactory.getLogger(StageMonitorInitializer.class);

	@Autowired
	private ServletContext servletContext;

	/**
	 * Calls {@link #onStartup(ServletContext)} with the right context.
	 */
	@PostConstruct
	public void onStartup() {
		onStartup(servletContext);
	}

	@Override
	public void onStartup(ServletContext ctx) {
		LOGGER.info("Initialising the StageMonitor plugin");
		new WebPlugin().onStartup(null, ctx);
	}
}
