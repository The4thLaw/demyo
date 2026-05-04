package org.demyo.service.impl;

import java.awt.AWTException;
import java.awt.Dimension;
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

import org.apache.commons.lang3.SystemUtils;
import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;



@Service(AWTSystemTrayService.QUALIFIER)
public class AWTSystemTrayService extends BaseSystemTrayService {
	public static final String QUALIFIER = "awtSystemTrayService";

	private static final Logger LOGGER = LoggerFactory.getLogger(AWTSystemTrayService.class);

	@Override
	public void createSystemTray() {
		SwingUtilities.invokeLater(this::createActualTray);
	}

	public void createActualTray() {
		final PopupMenu popup = new PopupMenu();

		MenuItem browserItem = new MenuItem(getBrowserStartLabel());
		browserItem.addActionListener(getBrowserStartAction());
		popup.add(browserItem);

		MenuItem exitItem = new MenuItem(getExitLabel());
		exitItem.addActionListener(getBaseExitAction(() -> {
			for (TrayIcon icon : SystemTray.getSystemTray().getTrayIcons()) {
				SystemTray.getSystemTray().remove(icon);
			}
		}));
		// Note: cannot add a shutdown hook to remove the icons, see https://bugs.openjdk.java.net/browse/JDK-8042114
		popup.add(exitItem);

		URL iconUrl = getMainIcon();
		BufferedImage iconBI;
		try {
			iconBI = ImageIO.read(iconUrl);
		} catch (IOException ioe) {
			LOGGER.warn("Failed to load the tray icon; there will be no GUI to close Demyo");
			return;
		}

		SystemTray tray = SystemTray.getSystemTray();

		// Resize icon to expected size.
		// The whole process is a bit quirky but it provides adequate results and is alread
		// a fallback for Dorkbox's approach
		Dimension trayIconSize = tray.getTrayIconSize();
		int dpi = Toolkit.getDefaultToolkit().getScreenResolution();
		int factor;
		if (SystemUtils.IS_OS_WINDOWS) {
			// Windows doesn't like larger icons, they end up overflowing
			factor = 1;
		} else {
			if (SystemUtils.IS_OS_MAC_OSX) {
				LOGGER.debug("MacOS detected, increasing reported DPI");
				dpi *= 2;
			}
			// 96 seems to be a bit of a magical number
			factor = (int) Math.ceil(dpi / (double) 96);
		}

		int targetWidth = trayIconSize.width * factor;
		int targetHeight = trayIconSize.height * factor;
		LOGGER.debug(
				"Tray icon size reported by Java is {}x{} with {} dpi, so applying factor {} we get {}x{}",
				trayIconSize.width, trayIconSize.height, dpi, factor, targetWidth, targetHeight);
		iconBI = Scalr.resize(iconBI, Scalr.Method.ULTRA_QUALITY, targetWidth, targetHeight, Scalr.OP_ANTIALIAS);

		// Won't be transparent. Again, it's a fallback
		// See http://bugs.java.com/bugdatabase/view_bug.do?bug_id=6453521
		TrayIcon trayIcon = new TrayIcon(iconBI, getMainTooltip());
		trayIcon.setPopupMenu(popup);
		try {
			tray.add(trayIcon);
		} catch (AWTException e) {
			LOGGER.warn("Failed to add a system tray icon; there will be no GUI to close Demyo");
			return;
		}

		LOGGER.info("AWT System tray menu is available");
	}
}
