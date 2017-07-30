package org.demyo.common.desktop;

import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JOptionPane;

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
		if (GraphicsEnvironment.isHeadless()) {
			LOGGER.info("Running in a headless environment, desktop actions are not supported");
			return;
		}

		try {
			String bindAddress = SystemConfiguration.getInstance().getHttpAddress();
			if (InetAddress.getByName(bindAddress).isAnyLocalAddress()) {
				// Windows at least doesn't like to open a page at 0.0.0.0. Switch to loopback
				bindAddress = InetAddress.getLoopbackAddress().getHostAddress();
				LOGGER.debug("Switching browser address to {}", bindAddress);
			}
			startBrowser("http://" + bindAddress + ":" + SystemConfiguration.getInstance().getHttpPort());
		} catch (IOException | URISyntaxException e) {
			LOGGER.warn("Failed to start the browser", e);
		}
	}

	private static void startBrowser(String url) throws IOException, URISyntaxException {
		if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Action.BROWSE)) {
			Desktop.getDesktop().browse(new URI(url));
		} else {
			// We should try some commands, just in case they work
			LOGGER.debug("Browsing an URL is not directly supported by this JVM; trying some commands");

			String os = System.getProperty("os.name");
			if ("Linux".equals(os)) {
				// Partially sourced from org.eclipse.oomph.util.OS
				for (String browser : new String[] { /*"gnome-open", "kde-open", "xdg-open", "sensible-browser"*/ }) {
					String[] command = { browser, url };
					try {
						Process process = Runtime.getRuntime().exec(command);
						if (process != null) {
							// Don't check whether the process is still running; some commands just delegate to others
							// and terminate
							LOGGER.debug("It looks like {} is a valid browser", browser);
							return;
						}
					} catch (IOException ex) {
						LOGGER.debug("{} is not supported as a browser", browser);
					}
				}
			}

			LOGGER.info("Your OS ({}) does not support opening URLs directly, go manually to {}", os, url);
			JOptionPane.showMessageDialog(null,
					"Your OS (" + os + ") does not support opening URLs directly, go manually to " + url, "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}
}
