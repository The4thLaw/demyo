package org.demyo.desktop;

import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SplashScreen;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.demyo.common.config.SystemConfiguration;
import org.demyo.common.exception.DemyoErrorCode;
import org.demyo.common.exception.DemyoRuntimeException;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.h2.jdbcx.JdbcDataSource;
import org.imgscalr.Scalr;
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
		if (!GraphicsEnvironment.isHeadless()) {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}

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
		new org.eclipse.jetty.plus.jndi.Resource("jdbc/demyoDataSource", ds);
		server.setHandler(webapp);

		server.start();

		if (!GraphicsEnvironment.isHeadless()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					createAndShowGUI(server);
				}

			});
		} else {
			LOGGER.info("System tray is not supported by the current system; there will be no GUI to close Demyo");
		}

		LOGGER.info("Demyo is now ready");
		SplashScreen splash = SplashScreen.getSplashScreen();
		if (splash != null) {
			try {
				splash.close();
				LOGGER.debug("Successfully closed the splash screen");
			} catch (IllegalStateException e) {
				LOGGER.debug("Splash screen already closed; no big deal");
			}
		}
		server.join();
	}

	private static void createAndShowGUI(final Server server) {
		if (SystemTray.isSupported()) {
			createSysTray(server);
		}

		if (Desktop.isDesktopSupported() && SystemConfiguration.getInstance().isAutoStartWebBrowser()) {
			startBrowser();
		}
	}

	private static void startBrowser() {
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
			LOGGER.warn("Failed to start the browser automatically", e);
		}
	}

	private static void createSysTray(final Server server) {
		final PopupMenu popup = new PopupMenu();

		// TODO: localise these. Use a ResourceBundle manually (store it in the demyo-app JAR)

		if (Desktop.isDesktopSupported()) {
			MenuItem browserItem = new MenuItem("Start Web browser");
			browserItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					startBrowser();
				}
			});
			popup.add(browserItem);
		}

		MenuItem exitItem = new MenuItem("Exit Demyo");
		exitItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				LOGGER.info("Stopping Demyo");
				for (TrayIcon icon : SystemTray.getSystemTray().getTrayIcons()) {
					SystemTray.getSystemTray().remove(icon);
				}
				// This should not be needed, but the EDT is still running :/
				/*for (Window w : Window.getWindows()) {
					LOGGER.debug("Disposing of window {}", w);
					w.dispose();
				}*/

				try {
					server.stop();
				} catch (Exception ex) {
					LOGGER.warn("Error while stopping server", ex);
				}
			}
		});
		popup.add(exitItem);

		URL iconUrl = Start.class.getResource("app-icon.png");
		assert (iconUrl != null);
		//ImageIcon icon = new ImageIcon(iconUrl, "Demyo tray icon");
		BufferedImage iconBI;
		try {
			iconBI = ImageIO.read(iconUrl);
		} catch (IOException ioe) {
			LOGGER.warn("Failed to load the tray icon; there will be no GUI to close Demyo");
			return;
		}

		// Resize icon to expected size
		SystemTray tray = SystemTray.getSystemTray();
		Dimension trayIconSize = tray.getTrayIconSize();
		LOGGER.debug("Tray icon size is (w x h): {} x {}", trayIconSize.width, trayIconSize.height);
		iconBI = Scalr.resize(iconBI, trayIconSize.width, trayIconSize.height, Scalr.OP_ANTIALIAS);

		TrayIcon trayIcon = new TrayIcon(iconBI, "Demyo");
		trayIcon.setPopupMenu(popup);
		try {
			tray.add(trayIcon);
		} catch (AWTException e) {
			LOGGER.warn("Failed to add a system tray icon; there will be no GUI to close Demyo");
			return;
		}

		LOGGER.info("System tray menu is available");
	}
}
