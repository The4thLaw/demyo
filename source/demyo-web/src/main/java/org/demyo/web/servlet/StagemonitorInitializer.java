package org.demyo.web.servlet;

import java.lang.reflect.InvocationTargetException;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.WebApplicationInitializer;

/**
 * Specific component to ensure the stagemonitor plugin is initialised. Has no compile-time dependencies on
 * stagemonitor, and no requirement at runtime.
 */
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

	/**
	 * Checks whether Stagemonitor is in the classpath. Does not check for potential issues with the initialisation.
	 * 
	 * @return <code>true</code> if the
	 */
	public static boolean isStagemonitorAvailable() {
		try {
			Class.forName("org.stagemonitor.web.servlet.initializer.ServletContainerInitializerUtil");
		} catch (ClassNotFoundException e) {
			return false;
		}
		return true;
	}

	@Override
	public void onStartup(ServletContext ctx) {
		try {
			Class<?> util = Class.forName("org.stagemonitor.web.servlet.initializer.ServletContainerInitializerUtil");
			util.getMethod("registerStagemonitorServletContainerInitializers", ServletContext.class).invoke(null,
					servletContext);
			LOGGER.info("Initialised the stagemonitor plugin");
		} catch (ClassNotFoundException e) {
			LOGGER.info("stagemonitor is not in the classpath, continuing happily");
		} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
			LOGGER.warn("stagemonitor is in the classpath but I can't initialize it, continuing happily", e);
		}
	}
}