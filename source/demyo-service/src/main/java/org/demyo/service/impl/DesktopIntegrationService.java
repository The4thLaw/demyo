package org.demyo.service.impl;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import jakarta.annotation.PostConstruct;

import org.apache.commons.lang3.SystemUtils;
import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.demyo.common.desktop.DesktopCallbacks;
import org.demyo.common.desktop.DesktopUtils;
import org.demyo.service.ITranslationService;

/**
 * A service with no public interface allowing integration with the user's desktop.
 */
@Service
public class DesktopIntegrationService {
	private static final Logger LOGGER = LoggerFactory.getLogger(DesktopIntegrationService.class);

	@Autowired
	private DesktopCallbacks desktop;

	@Autowired
	private ITranslationService translationService;

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
			String lfClass = UIManager.getSystemLookAndFeelClassName();
			UIManager.setLookAndFeel(lfClass);
			LOGGER.info("Look and Feel set to system ({})", lfClass);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			LOGGER.warn("Failed to set the Look and Feel", e);
		}

		SwingUtilities.invokeLater(this::createSysTray);
	}

	private void createSysTray() {
		final PopupMenu popup = new PopupMenu();

		MenuItem browserItem = new MenuItem(translationService.translate("desktop.tray.browser.label"));
		browserItem.addActionListener(e -> DesktopUtils.startBrowser());
		popup.add(browserItem);

		MenuItem exitItem = new MenuItem(translationService.translate("desktop.tray.exit.label"));
		exitItem.addActionListener(e -> {
			LOGGER.info("Stopping Demyo");
			for (TrayIcon icon : SystemTray.getSystemTray().getTrayIcons()) {
				SystemTray.getSystemTray().remove(icon);
			}

			desktop.stopServer();
		});
		// Note: cannot add a shutdown hook to remove the icons, see https://bugs.openjdk.java.net/browse/JDK-8042114
		popup.add(exitItem);

		String iconPath;
		if (SystemUtils.IS_OS_MAC_OSX) {
			// Use template images on OSX: let the OS decide the color depending on the theme
			LOGGER.debug("MacOS detected, the tray icon will be a template");
			System.setProperty("apple.awt.enableTemplateImages", "true");
			iconPath = "/org/demyo/common/desktop/app-icon-monochrome.png";
		} else {
			iconPath = "/org/demyo/common/desktop/app-icon.png";
		}
		URL iconUrl = getClass().getResource(iconPath);
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
		int dpi = Toolkit.getDefaultToolkit().getScreenResolution();
		if (SystemUtils.IS_OS_MAC_OSX) {
			// It's quite lame but I couldn't find a better way. Maybe using native APIs...
			// It's probably the same on Windows but since MacOS hardware will nearly always
			// be HiDPI...
			LOGGER.debug("MacOS detected, increasing reported DPI");
			dpi *= 2;
		}
		// 96 seems to be a bit of a magical number
		int factor = (int) Math.ceil(dpi / (double) 96);
		int targetWidth = trayIconSize.width * factor;
		int targetHeight = trayIconSize.height * factor;
		LOGGER.debug(
			"Tray icon size reported by Java is {}x{} with {} dpi, so applying factor {} we get {}x{}",
			trayIconSize.width, trayIconSize.height, dpi, factor, targetWidth, targetHeight);
		iconBI = Scalr.resize(iconBI, Scalr.Method.ULTRA_QUALITY, targetWidth, targetHeight, Scalr.OP_ANTIALIAS);

		// Won't be transparent :/
		// See http://bugs.java.com/bugdatabase/view_bug.do?bug_id=6453521
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
