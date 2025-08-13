package org.demyo.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import org.demyo.common.exception.DemyoErrorCode;
import org.demyo.common.exception.DemyoRuntimeException;

import dorkbox.systemTray.MenuItem;
import dorkbox.systemTray.SystemTray;

@Service(DorkboxSystemTrayService.QUALIFIER)
public class DorkboxSystemTrayService extends BaseSystemTrayService {
	public static final String QUALIFIER = "DorkboxSystemTrayService";

	private static final Logger LOGGER = LoggerFactory.getLogger(DorkboxSystemTrayService.class);

	@Override
	public void createSystemTray() {
		SystemTray systemTray = SystemTray.get();
		if (systemTray == null) {
			LOGGER.warn("Failed to use the Dorkbox system tray");
			throw new DemyoRuntimeException(DemyoErrorCode.SYS_TRAY_FAILING);
		}

		systemTray.setTooltip(getMainTooltip());
		systemTray.setImage(getMainIcon());
		systemTray.getMenu().add(new MenuItem(getBrowserStartLabel(), getBrowserStartAction()));
		systemTray.getMenu().add(new MenuItem(getExitLabel(), getBaseExitAction(systemTray::shutdown)));

		LOGGER.info("Dorkbox system tray menu is available");
	}
}
