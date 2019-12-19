package org.demyo.test;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.demyo.test.desktop.TestDesktopCallbacks;

/**
 * Base class for persistence tests.
 */
@RunWith(SpringJUnit4ClassRunner.class)
// TODO: try to solve https://stackoverflow.com/questions/27652689/spring-test-dbunit-warning
public abstract class AbstractPersistenceTest {
	protected static final int DEFAULT_PAGE_SIZE = 10;

	/**
	 * Initializes the JNDI information.
	 * 
	 * @throws NamingException If binding fails.
	 */
	@BeforeClass
	public static void setupJNDI() throws NamingException {
		SimpleNamingContextBuilder builder = SimpleNamingContextBuilder.emptyActivatedContextBuilder();
		DataSource ds = new DriverManagerDataSource("jdbc:h2:mem:demyo_tests;DB_CLOSE_DELAY=120;IGNORECASE=TRUE");
		builder.bind("org.demyo.services.dataSource", ds);

		builder.bind("org.demyo.services.desktop", new TestDesktopCallbacks());
	}
}
