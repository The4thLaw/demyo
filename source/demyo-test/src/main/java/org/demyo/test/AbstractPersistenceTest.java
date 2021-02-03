package org.demyo.test;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Base class for persistence tests.
 */
@ExtendWith(SpringExtension.class)
public abstract class AbstractPersistenceTest {
	/**
	 * Sets the JVM as headless to avoid having the DesktopIntegrationService creating a tray icon.
	 */
	@BeforeAll
	public static void setHeadless() {
		System.setProperty("java.awt.headless", "true");
	}
}
