package org.demyo.service.impl;

import java.awt.event.ActionListener;
import java.net.URL;

import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.demyo.common.desktop.DesktopCallbacks;
import org.demyo.common.desktop.DesktopUtils;
import org.demyo.service.ITranslationService;
import org.demyo.service.SystemTrayService;

public abstract class BaseSystemTrayService implements SystemTrayService {
	private static final Logger LOGGER = LoggerFactory.getLogger(BaseSystemTrayService.class);

	private static final String APP_ICON = "/org/demyo/common/desktop/app-icon.png";
	private static final String APP_ICON_MONOCHROME = "/org/demyo/common/desktop/app-icon-monochrome.png";

	@Autowired
	private DesktopCallbacks desktop;

	@Autowired
	protected ITranslationService translationService;

	protected URL getMainIcon() {
		String iconPath;
		if (SystemUtils.IS_OS_MAC_OSX) {
			// Use template images on OSX: let the OS decide the color depending on the theme
			LOGGER.debug("MacOS detected, the tray icon will be a template");
			System.setProperty("apple.awt.enableTemplateImages", "true");
			iconPath = APP_ICON_MONOCHROME;
		} else {
			iconPath = APP_ICON;
		}
		URL iconUrl = getClass().getResource(iconPath);
		assert (iconUrl != null);
		return iconUrl;
	}

	protected String getMainTooltip() {
		return translationService.translate("desktop.tray.mainIcon.tooltip");
	}

	protected String getBrowserStartLabel() {
		return translationService.translate("desktop.tray.browser.label");
	}

	protected static ActionListener getBrowserStartAction() {
		return e -> DesktopUtils.startBrowser();
	}

	protected String getExitLabel() {
		return translationService.translate("desktop.tray.exit.label");
	}

	protected ActionListener getBaseExitAction(Runnable r) {
		return e -> {
			LOGGER.info("Stopping Demyo");
			r.run();
			desktop.stopServer();
		};
	}
}
