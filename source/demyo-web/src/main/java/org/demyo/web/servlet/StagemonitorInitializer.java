package org.demyo.web.servlet;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.WebApplicationInitializer;

/**
 * Specific component to ensure the stagemonitor plugin is initialised. Has no compile-time dependencies on
 * stagemonitor, and no requirement at runtime.
 */
// TODO: Fix ITs
@Component
public class StagemonitorInitializer implements WebApplicationInitializer {
	private static final Logger LOGGER = LoggerFactory.getLogger(StagemonitorInitializer.class);

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
		try {
			Class<?> webPluginClass = Class.forName("org.stagemonitor.web.WebPlugin");
			ServletContainerInitializer webPlugin = (ServletContainerInitializer) webPluginClass.newInstance();
			webPlugin.onStartup(null, ctx);
			LOGGER.info("Initialised the stagemonitor plugin");
		} catch (ClassNotFoundException e) {
			LOGGER.info("stagemonitor is not in the classpath, continuing happily");
		} catch (InstantiationException | IllegalAccessException | ClassCastException e) {
			LOGGER.warn("stagemonitor is in the classpath but I can't instantiate it, continuing happily", e);
		} catch (ServletException | UnsupportedOperationException e) {
			// UnsupportedOperationException could happen in integration tests, for example. We could exclude
			// stagemonitor for failsafe tests but Eclipse does not comply
			LOGGER.warn("stagemonitor is in the classpath but I can't configure it, continuing happily", e);
		}
	}
}
