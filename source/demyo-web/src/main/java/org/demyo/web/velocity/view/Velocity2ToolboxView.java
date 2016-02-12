package org.demyo.web.velocity.view;

import java.io.IOException;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.config.FileFactoryConfiguration;
import org.apache.velocity.tools.config.XmlFactoryConfiguration;
import org.apache.velocity.tools.view.ViewToolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.ServletContextResource;
import org.springframework.web.servlet.view.velocity.VelocityLayoutView;

/**
 * Velocity Tools 2-compatible view. As of Spring 3.2, the framework still relies on obsolete API.
 * <p>
 * Unlike the method at {@link http://www.elepha.info/article/305-configuring-velocity-tools-20-in-spring.html},
 * this class does not re-initialize all toolboxes for each request. And unlike the method at {@link http
 * ://www.sergiy.ca/how-to-integrate-velocity-tools-2-0-with-spring/}, it actually initializes the tools depending
 * on what they need from the HTTP request and response, making them functional (LinkTool used to fail).
 * </p>
 * <p>
 * This class caches the tool configuration so that most of the work is only done once.
 * </p>
 * 
 * @see https://velocity.apache.org/tools/devel/standalone.html
 */
public class Velocity2ToolboxView extends VelocityLayoutView {
	private static final Logger LOGGER = LoggerFactory.getLogger(Velocity2ToolboxView.class);

	private ViewToolManager velocityToolManager = null;

	@PostConstruct
	private void initToolManager() throws IllegalStateException, IOException {
		LOGGER.debug("Configuring toolbox from {}", getToolboxConfigLocation());
		velocityToolManager = new ViewToolManager(getServletContext(), false, true);
		velocityToolManager.setVelocityEngine(getVelocityEngine());
		FileFactoryConfiguration factoryConfig = new XmlFactoryConfiguration(false);
		factoryConfig.read(new ServletContextResource(getServletContext(), getToolboxConfigLocation()).getURL());
		velocityToolManager.configure(factoryConfig);
	}

	@Override
	protected Context createVelocityContext(Map<String, Object> model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		VelocityContext context = new VelocityContext(velocityToolManager.createContext(request, response));
		if (model != null) {
			for (Map.Entry<String, Object> entry : model.entrySet()) {
				context.put(entry.getKey(), entry.getValue());
			}
		}
		return context;
	}
}
