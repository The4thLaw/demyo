package org.demyo.desktop;

import java.awt.GraphicsEnvironment;
import java.awt.SplashScreen;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.file.Path;

import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.context.ConfigurableApplicationContext;

import org.demyo.common.config.SystemConfiguration;

/**
 * Main entry point for Demyo operation.
 */
@SpringBootApplication(scanBasePackages = "org.demyo")
public final class Demyo {
	private static final Logger LOGGER = LoggerFactory.getLogger(Demyo.class);

	private Demyo() {
	}

	/**
	 * Startup method.
	 *
	 * @param args The command line arguments.
	 */
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(Demyo.class);
		Path pidFile = SystemConfiguration.getInstance().getUserDirectory().resolve("demyo.pid");
		ApplicationPidFileWriter pidWriter = new ApplicationPidFileWriter(pidFile.toFile());
		app.addListeners(pidWriter);
		ConfigurableApplicationContext ctx = app.run(args);

		String host = ctx.getEnvironment().getProperty("server.address");
		String port = ctx.getEnvironment().getProperty("server.port");
		String context = ctx.getEnvironment().getProperty("server.servlet.context-path");
		if (host == null) {
			LOGGER.info("Demyo is now ready on:");
			try {
				NetworkInterface.networkInterfaces()
					.flatMap(NetworkInterface::inetAddresses)
					.filter(i -> {
						if (i instanceof Inet4Address) {
							return true;
						} else if (i instanceof Inet6Address i6) {
							// Filter out scoped addresses
							return i6.getScopeId() == 0;
						}
						return false;
					})
					.map(i -> {
						if (i instanceof Inet6Address) {
							return "[" + i.getHostAddress() + "]";
						}
						return i.getHostAddress();
					})
					.forEach(h -> LOGGER.info("\thttp://{}:{}{}", h, port, context));
			} catch (SocketException e) {
				LOGGER.info("\t\t<failed to get network interfaces, Demyo is still available>");
			}
		} else {
			LOGGER.info("Demyo is now ready on http://{}:{}{}", host, port, context);
		}

		LOGGER.info("PID file is in {}", pidFile.toAbsolutePath());

		closeSplashScreen();
	}

	/**
	 * Startup method (legacy version).
	 *
	 * @param args The command line arguments.
	 */
	/*public static void mainLegacy(String[] args) {
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
	}*/

	/*private static void startDemyo() throws Exception {
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
	}*/

	/*private static Server startHttpServer()
			throws Exception {
		SystemConfiguration sysConfig = SystemConfiguration.getInstance();
		String httpAddress = sysConfig.getHttpAddress();
		int httpPort = sysConfig.getHttpPort();
		String contextRoot = sysConfig.getContextRoot();
		LOGGER.info("Starting server on {}:{}{} ...", httpAddress, httpPort, contextRoot);

		final Server server = new Server(new InetSocketAddress(httpAddress, httpPort));

		WebAppContext webapp = new WebAppContext();
		// Needed to help Jetty find the JAR from Glassfish containing the JSTL implementations
		webapp.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern", ".*\/[^/]*jstl.*\\.jar$");
		webapp.setContextPath(contextRoot);
		webapp.setWar(sysConfig.getWarPath());
		webapp.setThrowUnavailableOnStartupException(true);
		webapp.setTempDirectory(sysConfig.getTempDirectory().resolve("jetty").toFile());

		setDesktopCallbacks(server);
		server.setHandler(webapp);

		server.start();
		return server;
	}*/

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
	/*private static void setDesktopCallbacks(final Server server) throws NamingException {
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
	}*/

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
		} else {
			LOGGER.trace("There is no splash screen");
		}
	}
}
