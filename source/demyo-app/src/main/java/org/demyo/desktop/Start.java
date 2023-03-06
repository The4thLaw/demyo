package org.demyo.desktop;

import java.awt.GraphicsEnvironment;
import java.awt.SplashScreen;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.naming.NamingException;
import javax.swing.JOptionPane;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.h2.engine.Constants;
import org.h2.jdbcx.JdbcDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.demyo.common.config.SystemConfiguration;
import org.demyo.common.desktop.DesktopCallbacks;
import org.demyo.common.desktop.DesktopUtils;
import org.demyo.common.exception.DemyoErrorCode;
import org.demyo.common.exception.DemyoRuntimeException;

/**
 * Main entry point for Demyo operation.
 */
public final class Start {
	private static final Logger LOGGER = LoggerFactory.getLogger(Start.class);
	private static final String DB_USER = "demyo";
	private static final String DB_PASSWORD = "demyo";
	private static final String DB_FILE_SUFFIX = Pattern.quote(Constants.SUFFIX_MV_FILE) + "$";
	private static final int DEMYO_3_0_H2_VERSION = 196;

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
			String path = SystemConfiguration.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			String decodedPath;
			try {
				decodedPath = URLDecoder.decode(path, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new DemyoRuntimeException(DemyoErrorCode.SYS_APPDIR_NOT_FOUND, e);
			}
			// Go two directories above: from JAR to lib dir to app dir
			System.setProperty("demyo.applicationDirectory",
					new File(decodedPath).getParentFile().getParentFile().getAbsolutePath());
		}

		JdbcDataSource ds = startDatabase();
		Server server = startHttpServer(ds);

		SystemConfiguration sysConfig = SystemConfiguration.getInstance();
		if (sysConfig.isAutoStartWebBrowser()) {
			DesktopUtils.startBrowser();
		}

		LOGGER.info("Demyo is now ready");
		closeSplashScreen();
		server.join();
	}

	private static Server startHttpServer(JdbcDataSource ds)
			throws Exception {
		SystemConfiguration sysConfig = SystemConfiguration.getInstance();
		String httpAddress = sysConfig.getHttpAddress();
		int httpPort = sysConfig.getHttpPort();
		String contextRoot = sysConfig.getContextRoot();
		LOGGER.info("Starting server on {}:{}{} ...", httpAddress, httpPort, contextRoot);

		final Server server = new Server(new InetSocketAddress(httpAddress, httpPort));

		Configuration.ClassList classlist = Configuration.ClassList.setServerDefault(server);
		classlist.addBefore(
				// Needed for proper JSP/JSTL support
				"org.eclipse.jetty.webapp.JettyWebXmlConfiguration",
				"org.eclipse.jetty.annotations.AnnotationConfiguration");

		WebAppContext webapp = new WebAppContext();
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

		new org.eclipse.jetty.plus.jndi.Resource("org.demyo.services.dataSource", ds);
		setDesktopCallbacks(server);
		server.setHandler(webapp);

		server.start();
		return server;
	}

	private static JdbcDataSource startDatabase()
			throws IOException, SQLException, ReflectiveOperationException {
		SystemConfiguration sysConfig = SystemConfiguration.getInstance();
		Path databaseFile = sysConfig.getDatabaseFile();
		boolean isNewDatabase = !Files.exists(databaseFile);
		// To debug, use java -cp h2-*.jar org.h2.tools.Console
		String databaseFilePath = databaseFile.toAbsolutePath().toString().replaceAll(DB_FILE_SUFFIX, "");

		String url = "jdbc:h2:" + databaseFilePath + ";DB_CLOSE_DELAY=120;IGNORECASE=TRUE";
		LOGGER.debug("Database URL is {}", url);

		migrateH2IfNeeded(databaseFile, isNewDatabase, url);

		LOGGER.info("Starting database...");
		JdbcDataSource ds = new JdbcDataSource();
		ds.setURL(url);
		ds.setUser(DB_USER);
		ds.setPassword(DB_PASSWORD);

		if (isNewDatabase) {
			LOGGER.info("Setting the database collation...");
			// This is the collation for French, but it should do no harm to English
			try (Connection connec = ds.getConnection(); Statement stmt = connec.createStatement()) {
				stmt.execute("SET DATABASE COLLATION French STRENGTH PRIMARY;");
			}
		}
		return ds;
	}

	private static void migrateH2IfNeeded(Path databaseFile, boolean isNewDatabase, String url)
			throws IOException, SQLException, ReflectiveOperationException {
		Path databaseVersionFile = databaseFile.getParent().resolve("demyo-h2-version.txt");
		if (!isNewDatabase) {
			LOGGER.debug("Checking if the database's H2 version must be migrated");
			int version;
			if (!Files.exists(databaseVersionFile)) {
				version = DEMYO_3_0_H2_VERSION;
			} else {
				String versionStr = new String(Files.readAllBytes(databaseVersionFile), StandardCharsets.UTF_8);
				version = Integer.parseInt(versionStr);
			}

			if (version == DEMYO_3_0_H2_VERSION) {
				// H2 at the version of Demyo 3.0 supported stuff that it shouldn't have and on which migrations
				// relied:
				// - UNSIGNED INT as a datatype
				// - Dangling commas at the end of a list of columns in a create statement
				// - MODIFY COLUMN could be used rather than ALTER COLUMN
				// So we need to repair
				LOGGER.info("Migrating from H2 {} requires a Flyway repair", version);
				SystemConfiguration.getInstance().setFlywayRepairRequired(true);
			}

			if (version != Constants.BUILD_ID) {
				LOGGER.info("Migrating the H2 database from {} to {}", version, Constants.BUILD_ID);
				Properties dbMigProps = new Properties();
				dbMigProps.setProperty("user", DB_USER);
				dbMigProps.setProperty("password", DB_PASSWORD);
				H2LocalUpgrade.upgrade(url, dbMigProps, version);
			} else {
				LOGGER.debug("The H2 database is at version {}, same as what Demyo requires", version);
			}
		}

		// Always write the current H2 version
		Files.write(databaseVersionFile, String.valueOf(Constants.BUILD_ID).getBytes(StandardCharsets.UTF_8));
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
