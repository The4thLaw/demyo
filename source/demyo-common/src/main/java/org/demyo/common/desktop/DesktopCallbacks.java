package org.demyo.common.desktop;

/**
 * Callbacks from the desktop application to help the Spring context interact with the user's desktop.
 * <p>
 * This class avoids having a dependency from the Spring application to a specific server implementation.
 * </p>
 */
public interface DesktopCallbacks {
	/**
	 * Stops the server running Demyo.
	 */
	void stopServer();
}
