package org.demyo.desktop;

import java.io.File;

import org.demyo.model.config.SystemConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.h2.jdbcx.JdbcDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main entry point for Demyo operation.
 * 
 * @author $Author: xr $
 * @version $Revision: 1055 $
 */
public class Start {
	private static final Logger LOGGER = LoggerFactory.getLogger(Start.class);

	public static void main(String[] args) throws Exception {

		File databaseFile = SystemConfiguration.getInstance().getDatabaseFile();
		boolean isNewDatabase = !databaseFile.exists();
		// To debug, use java -cp h2-*.jar org.h2.tools.Console
		String databaseFilePath = databaseFile.getAbsolutePath().replaceAll("\\.h2\\.db$", "");

		LOGGER.info("Starting database...");
		JdbcDataSource ds = new JdbcDataSource();
		ds.setURL("jdbc:h2:" + databaseFilePath + ";DB_CLOSE_DELAY=120;IGNORECASE=TRUE");
		ds.setUser("sa");
		ds.setPassword("sa");

		if (isNewDatabase) {
			LOGGER.info("Creating the database");
			ds.getConnection().createStatement()
					.execute("RUNSCRIPT FROM 'classpath:/org/demyo/model/schema/create-tables.sql'");
			LOGGER.debug("Created the database");
		}

		LOGGER.info("Starting server...");

		Server server = new Server(SystemConfiguration.getInstance().getHttpPort());

		WebAppContext webapp = new WebAppContext();
		webapp.setContextPath("/");
		webapp.setWar("../demyo-web/target/demyo-web-2.0.0-alpha1");
		new org.eclipse.jetty.plus.jndi.Resource("jdbc/demyoDataSource", ds);
		server.setHandler(webapp);

		server.start();
		LOGGER.info("Demyo is now ready");
		server.join();
	}
}
