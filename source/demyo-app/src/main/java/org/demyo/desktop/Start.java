package org.demyo.desktop;

import java.awt.GraphicsEnvironment;
import java.awt.SplashScreen;
import java.net.InetSocketAddress;
import java.nio.file.Path;

import javax.naming.NamingException;
import javax.swing.JOptionPane;

import org.eclipse.jetty.ee10.webapp.WebAppContext;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.demyo.common.config.SystemConfiguration;
import org.demyo.common.desktop.DesktopCallbacks;
import org.demyo.common.desktop.DesktopUtils;

/**
 * Main entry point for Demyo operation.
 */
public final class Start {
	private static final Logger LOGGER = LoggerFactory.getLogger(Start.class);

	private Start() {
	}

	/**
	 * Startup method.
	 *
	 * @param args The command line arguments.
	 */
	public static void main(String[] args) {
		try {
			startDemyo();
		} catch (Exception e) {
			LOGGER.error("Failed to start Demyo", e);
			if (!GraphicsEnvironment.isHeadless()) {
				JOptionPane.showMessageDialog(null,
						"Failed to start Demyo. More information may be available in the logs.", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}

		LOGGER.debug("Main method exiting");
		// This should not be needed, but the EDT hangs on.
		System.exit(0);
	}

	private static void startDemyo() throws Exception {
		// Try to detect the application directory, based on the app JAR (the only reliable one since others may come
		// from the exploded WAR).
		if (System.getProperty("demyo.applicationDirectory") == null) {
			Path jarPath = Path.of(SystemConfiguration.class.getProtectionDomain().getCodeSource().getLocation().toURI());
			// Go two directories above: from JAR to lib dir to app dir
			System.setProperty("demyo.applicationDirectory",
				jarPath.getParent().getParent().toAbsolutePath().toString());
		}

		Server server = startHttpServer();

		SystemConfiguration sysConfig = SystemConfiguration.getInstance();
		if (sysConfig.isAutoStartWebBrowser()) {
			DesktopUtils.startBrowser();
		}

		LOGGER.info("Demyo is now ready");
		closeSplashScreen();
		server.join();
	}

	private static Server startHttpServer()
			throws Exception {
		SystemConfiguration sysConfig = SystemConfiguration.getInstance();
		String httpAddress = sysConfig.getHttpAddress();
		int httpPort = sysConfig.getHttpPort();
		String contextRoot = sysConfig.getContextRoot();
		LOGGER.info("Starting server on {}:{}{} ...", httpAddress, httpPort, contextRoot);

		final Server server = new Server(new InetSocketAddress(httpAddress, httpPort));

		WebAppContext webapp = new WebAppContext();
		// Needed to help Jetty find the JAR from Glassfish containing the JSTL implementations
		webapp.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern", ".*/[^/]*jstl.*\\.jar$");
		webapp.setContextPath(contextRoot);
		webapp.setWar(sysConfig.getWarPath());
		webapp.setThrowUnavailableOnStartupException(true);
		webapp.setTempDirectory(sysConfig.getTempDirectory().resolve("jetty").toFile());

		// Set paths to plugins
		PluginManager pluginMgr = new PluginManager(sysConfig.getSystemPluginDirectory(),
				sysConfig.getUserPluginDirectory());
		String extraClasspath = pluginMgr.getPluginPaths();
		LOGGER.info("Setting extra classpath for Jetty: {}", extraClasspath);
		webapp.setExtraClasspath(extraClasspath);

		setDesktopCallbacks(server);
		server.setHandler(webapp);

		server.start();
		return server;
	}

	/**
	 * Sets the required {@link DesktopCallbacks} for the application.
	 * <p>
	 * The callbacks are published to JNDI under <code>org.demyo.services.desktop</code>.
	 * </p>
	 * <p>
	 * This method must be called before starting the server.
	 * </p>
	 *
	 * @param server The Jetty server
	 * @throws NamingException In case registering the JNDI attribute fails.
	 */
	private static void setDesktopCallbacks(final Server server) throws NamingException {
		new org.eclipse.jetty.plus.jndi.Resource("org.demyo.services.desktop", new DesktopCallbacks() {
			@Override
			public void stopServer() {
				try {
					server.stop();
				} catch (Exception ex) {
					LOGGER.warn("Error while stopping server", ex);
				}
			}
		});
	}

	private static void closeSplashScreen() {
		if (GraphicsEnvironment.isHeadless()) {
			return;
		}
		SplashScreen splash = SplashScreen.getSplashScreen();
		if (splash != null) {
			try {
				splash.close();
				LOGGER.debug("Successfully closed the splash screen");
			} catch (IllegalStateException e) {
				LOGGER.debug("Splash screen already closed; no big deal");
			}
		}
	}
}
