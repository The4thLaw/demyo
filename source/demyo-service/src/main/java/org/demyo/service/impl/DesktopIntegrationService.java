package org.demyo.service.impl;

import java.awt.GraphicsEnvironment;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import org.demyo.service.SystemTrayService;

/**
 * A service with no public interface allowing integration with the user's desktop.
 */
@Service
public class DesktopIntegrationService {
	private static final Logger LOGGER = LoggerFactory.getLogger(DesktopIntegrationService.class);

	@Autowired
	@Qualifier(AWTSystemTrayService.QUALIFIER)
	private SystemTrayService awtTrayService;

	@Autowired
	@Qualifier(DorkboxSystemTrayService.QUALIFIER)
	private SystemTrayService dorkTrayService;

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

		try {
			dorkTrayService.createSystemTray();
		} catch (RuntimeException e) {
			awtTrayService.createSystemTray();
		}
	}

}
