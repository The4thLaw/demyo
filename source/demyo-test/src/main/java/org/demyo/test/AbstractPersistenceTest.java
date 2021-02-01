package org.demyo.test;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Base class for persistence tests.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public abstract class AbstractPersistenceTest {
	/**
	 * Sets the JVM as headless to avoid having the DesktopIntegrationService creating a tray icon.
	 */
	@BeforeClass
	public static void setHeadless() {
		System.setProperty("java.awt.headless", "true");
	}
}
