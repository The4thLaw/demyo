package org.demyo.desktop;

import java.awt.Desktop;
import java.awt.GraphicsEnvironment;
import java.awt.SplashScreen;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;

import javax.naming.NamingException;
import javax.swing.JOptionPane;

import org.demyo.common.config.SystemConfiguration;
import org.demyo.common.desktop.DesktopCallbacks;
import org.demyo.common.desktop.DesktopUtils;
import org.demyo.common.exception.DemyoErrorCode;
import org.demyo.common.exception.DemyoRuntimeException;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.h2.jdbcx.JdbcDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
			startDemyo(args);
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

	private static void startDemyo(String[] args) throws Exception {
		// Try to detect the application directory, based on the app JAR (the only reliable one since others may come
		// from the exploded WAR).
		if (System.getProperty("demyo.applicationDirectory") == null) {
			String path = SystemConfiguration.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			String decodedPath;
			try {
				decodedPath = URLDecoder.decode(path, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new DemyoRuntimeException(DemyoErrorCode.SYS_APPDIR_NOT_FOUND, e);
			}
			// Go two directories above: from JAR to lib dir to app dir
			System.setProperty("demyo.applicationDirectory", new File(decodedPath).getParentFile().getParentFile()
					.getAbsolutePath());
		}

		File databaseFile = SystemConfiguration.getInstance().getDatabaseFile();
		boolean isNewDatabase = !databaseFile.exists();
		// To debug, use java -cp h2-*.jar org.h2.tools.Console
		String databaseFilePath = databaseFile.getAbsolutePath().replaceAll("\\.h2\\.db$", "");

		LOGGER.info("Starting database...");
		JdbcDataSource ds = new JdbcDataSource();
		ds.setURL("jdbc:h2:" + databaseFilePath + ";DB_CLOSE_DELAY=120;IGNORECASE=TRUE");
		ds.setUser("demyo");
		ds.setPassword("demyo");

		if (isNewDatabase) {
			LOGGER.info("Setting the database collation...");
			// This is the collation for French, but it should do no harm to English
			ds.getConnection().createStatement().execute("SET DATABASE COLLATION French STRENGTH PRIMARY;");
		}

		String httpAddress = SystemConfiguration.getInstance().getHttpAddress();
		int httpPort = SystemConfiguration.getInstance().getHttpPort();
		LOGGER.info("Starting server on {}:{} ...", httpAddress, httpPort);

		final Server server = new Server(new InetSocketAddress(httpAddress, httpPort));

		WebAppContext webapp = new WebAppContext();
		webapp.setContextPath("/");
		webapp.setWar(SystemConfiguration.getInstance().getWarPath());
		webapp.setThrowUnavailableOnStartupException(true);
		new org.eclipse.jetty.plus.jndi.Resource("org.demyo.services.dataSource", ds);
		setDesktopCallbacks(server);
		server.setHandler(webapp);

		server.start();

		if (Desktop.isDesktopSupported() && SystemConfiguration.getInstance().isAutoStartWebBrowser()) {
			DesktopUtils.startBrowser();
		}

		LOGGER.info("Demyo is now ready");
		closeSplashScreen();
		server.join();
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
