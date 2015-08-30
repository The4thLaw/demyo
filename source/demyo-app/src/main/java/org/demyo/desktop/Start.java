package org.demyo.desktop;

import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.demyo.model.config.SystemConfiguration;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.h2.jdbcx.JdbcDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main entry point for Demyo operation.
 * 
 * @author $Author: xr $
 * @version $Revision: 1055 $
 */
public class Start {
	private static final Logger LOGGER = LoggerFactory.getLogger(Start.class);

	public static void main(String[] args) throws Exception {

		File databaseFile = SystemConfiguration.getInstance().getDatabaseFile();
		boolean isNewDatabase = !databaseFile.exists();
		// To debug, use java -cp h2-*.jar org.h2.tools.Console
		String databaseFilePath = databaseFile.getAbsolutePath().replaceAll("\\.h2\\.db$", "");

		LOGGER.info("Starting database...");
		JdbcDataSource ds = new JdbcDataSource();
		ds.setURL("jdbc:h2:" + databaseFilePath + ";DB_CLOSE_DELAY=120;IGNORECASE=TRUE");
		ds.setUser("sa");
		ds.setPassword("sa");

		if (isNewDatabase) {
			LOGGER.info("Creating the database");
			ds.getConnection().createStatement()
					.execute("RUNSCRIPT FROM 'classpath:/org/demyo/model/schema/create-tables.sql'");
			LOGGER.debug("Created the database");
		}

		LOGGER.info("Starting server...");

		final Server server = new Server(SystemConfiguration.getInstance().getHttpPort());

		WebAppContext webapp = new WebAppContext();
		webapp.setContextPath("/");
		webapp.setWar("../demyo-web/target/demyo-web-2.0.0-alpha1");
		new org.eclipse.jetty.plus.jndi.Resource("jdbc/demyoDataSource", ds);
		server.setHandler(webapp);

		server.start();

		if (SystemTray.isSupported()) {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

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
		server.join();
		LOGGER.debug("Main method exiting");
		// This should not be needed, but the EDT hangs on.
		System.exit(0);
	}

	private static void createAndShowGUI(final Server server) {
		final PopupMenu popup = new PopupMenu();
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
		ImageIcon icon = new ImageIcon(iconUrl, "Demyo tray icon");
		TrayIcon trayIcon = new TrayIcon(icon.getImage(), "Demyo"); // TODO: resize icon to expected size
		trayIcon.setPopupMenu(popup);
		try {
			SystemTray.getSystemTray().add(trayIcon);
		} catch (AWTException e) {
			LOGGER.warn("Failed to add a system tray icon; there will be no GUI to close Demyo");
		}

		LOGGER.info("System tray menu is available");
	}
}
