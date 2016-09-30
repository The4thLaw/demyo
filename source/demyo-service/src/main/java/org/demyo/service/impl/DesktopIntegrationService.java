package org.demyo.service.impl;

import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.demyo.common.desktop.DesktopCallbacks;
import org.demyo.common.desktop.DesktopUtils;

import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * A service with no public interface allowing integration with the user's desktop.
 */
@Service
public class DesktopIntegrationService {
	private static final Logger LOGGER = LoggerFactory.getLogger(DesktopIntegrationService.class);

	@Autowired
	private DesktopCallbacks desktop;

	@Autowired
	private TranslationService translationService;

	/**
	 * Initializes the desktop integration.
	 */
	@PostConstruct
	private void init() {
		if (GraphicsEnvironment.isHeadless()) {
			LOGGER.info("The current system is headless; there will be no GUI to close Demyo");
			return;
		}

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			LOGGER.warn("Failed to set the Look and Feel", e);
		}

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				createSysTray();
			}
		});
	}

	private void createSysTray() {
		final PopupMenu popup = new PopupMenu();

		if (Desktop.isDesktopSupported()) {
			MenuItem browserItem = new MenuItem(translationService.translate("desktop.tray.browser.label"));
			browserItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					DesktopUtils.startBrowser();
				}
			});
			popup.add(browserItem);
		}

		MenuItem exitItem = new MenuItem(translationService.translate("desktop.tray.exit.label"));
		exitItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				LOGGER.info("Stopping Demyo");
				for (TrayIcon icon : SystemTray.getSystemTray().getTrayIcons()) {
					SystemTray.getSystemTray().remove(icon);
				}

				desktop.stopServer();
			}
		});
		popup.add(exitItem);

		URL iconUrl = getClass().getResource("/org/demyo/common/desktop/app-icon.png");
		assert (iconUrl != null);
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

		TrayIcon trayIcon = new TrayIcon(iconBI, translationService.translate("desktop.tray.mainIcon.tooltip"));
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