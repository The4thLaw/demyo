package org.demyo.test.desktop;

import org.demyo.common.desktop.DesktopCallbacks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * Implementation of {@link DesktopCallbacks} suitable for unit and integration tests.
 */
@Component
@Primary
// To have Spring use this one in case another is defined through JNDI
public class TestDesktopCallbacks implements DesktopCallbacks {
	private static final Logger LOGGER = LoggerFactory.getLogger(TestDesktopCallbacks.class);

	@Override
	public void stopServer() {
		LOGGER.debug("Asked to stop server.");
	}

}
