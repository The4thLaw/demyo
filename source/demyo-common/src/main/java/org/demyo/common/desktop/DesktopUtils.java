package org.demyo.common.desktop;

import java.awt.Desktop;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;

import org.demyo.common.config.SystemConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilities for Desktop integration.
 */
public final class DesktopUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(DesktopUtils.class);

	private DesktopUtils() {
		// Utility class
	}

	/**
	 * Starts the user's default browser and directs him to the running Demyo instance.
	 */
	public static void startBrowser() {
		try {
			String bindAddress = SystemConfiguration.getInstance().getHttpAddress();
			if (InetAddress.getByName(bindAddress).isAnyLocalAddress()) {
				// Windows at least doesn't like to open a page at 0.0.0.0. Switch to loopback
				bindAddress = InetAddress.getLoopbackAddress().getHostAddress();
				LOGGER.debug("Switching browser address to {}", bindAddress);
			}
			Desktop.getDesktop().browse(
					new URI("http://" + bindAddress + ":" + SystemConfiguration.getInstance().getHttpPort()));
		} catch (IOException | URISyntaxException e) {
			LOGGER.warn("Failed to start the browser", e);
		}
	}
}
