package org.demyo.dao.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.ClassicConfiguration;
import org.h2.jdbcx.JdbcDataSource;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.config.BootstrapMode;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.the4thlaw.commons.utils.h2.H2LocalUpgrader;
import org.the4thlaw.commons.utils.h2.H2VersionManager;

import org.demyo.common.config.SystemConfiguration;
import org.demyo.common.exception.DemyoErrorCode;
import org.demyo.common.exception.DemyoRuntimeException;
import org.demyo.dao.utils.FlywayUtils;

/**
 * Configuration for the DAO layer (mainly database and migrations).
 */
@Configuration
@ComponentScan("org.demyo")
@EnableJpaRepositories(basePackages = "org.demyo.dao", bootstrapMode = BootstrapMode.DEFERRED)
@EnableTransactionManagement
public class DaoConfig {
	private static final Logger LOGGER = LoggerFactory.getLogger(DaoConfig.class);
	private static final String DS_BEAN_NAME = "dataSource";
	private static final String FLYWAY_BEAN_NAME = "flyway";

	private static final String DB_USER = "demyo";
	private static final String DB_PASSWORD = "demyo";
	/** The version of H2 used before we started tracking the version numbers. */
	private static final int DEMYO_3_0_H2_VERSION = 196;

	@Value("${spring.jpa.show-sql}")
	private String showSql;
	@Value("${spring.jpa.properties.hibernate.format_sql}")
	private String formatSql;

	public DaoConfig() {
		LOGGER.info("Parsing configuration");
	}

	@Bean(name = DS_BEAN_NAME)
	public DataSource dataSource() throws SQLException {
		// To debug, use java -cp h2-*.jar org.h2.tools.Console

		SystemConfiguration sysConfig = SystemConfiguration.getInstance();
		Path databaseFile = sysConfig.getDatabaseFile();
		boolean isNewDatabase = !Files.exists(databaseFile);

		String databaseFilePath = sysConfig.getDatabaseUrlPath().toString();
		String url = "jdbc:h2:" + databaseFilePath + ";DB_CLOSE_DELAY=120;IGNORECASE=TRUE";
		LOGGER.debug("Database URL is {}", url);

		// Potentially migrate the database
		migrateH2IfNeeded(isNewDatabase, Path.of(databaseFilePath).getParent(), url);

		LOGGER.info("Starting database...");
		JdbcDataSource ds = new JdbcDataSource();
		ds.setURL(url);
		ds.setUser(DB_USER);
		ds.setPassword(DB_PASSWORD);

		if (isNewDatabase) {
			LOGGER.info("Setting the database collation...");
			// This is the collation for French, but it should do no harm to English
			try (Connection connec = ds.getConnection(); Statement stmt = connec.createStatement()) {
				stmt.execute("SET DATABASE COLLATION French STRENGTH PRIMARY;");
			}
		}
		return ds;
	}

	private static void migrateH2IfNeeded(boolean isNewDatabase, Path databaseFilePath, String url) {
		Path h2CacheDirectory;
		String h2CacheProperty = System.getProperty("demyo.h2.cacheDirectoryName");
		if (h2CacheProperty != null) {
			h2CacheDirectory = Path.of(h2CacheProperty);
		} else {
			h2CacheDirectory = SystemConfiguration.getInstance().getApplicationDirectory()
					.resolve("legacy-h2-versions");
		}
		H2LocalUpgrader upgrader = new H2LocalUpgrader(h2CacheDirectory);
		H2VersionManager vm = new H2VersionManager(DEMYO_3_0_H2_VERSION, databaseFilePath, upgrader);
		// Get the version before the upgrade
		int oldVersion = vm.getCurrentVersion();
		vm.migrateH2IfNeeded(isNewDatabase, url, DB_USER, DB_PASSWORD);

		if (!isNewDatabase && oldVersion == DEMYO_3_0_H2_VERSION) {
			// H2 at the version of Demyo 3.0 supported stuff that it shouldn't have and on which migrations
			// relied:
			// - UNSIGNED INT as a datatype
			// - Dangling commas at the end of a list of columns in a create statement
			// - MODIFY COLUMN could be used rather than ALTER COLUMN
			// So we need to repair because the SQL files were changes accordingly and their hashes have changed
			LOGGER.info("Migrating from H2 {} requires a Flyway repair", oldVersion);
			SystemConfiguration.getInstance().setFlywayRepairRequired(true);
		}
	}

	@Bean(name = FLYWAY_BEAN_NAME, initMethod = "migrate")
	// Must be run after the Datasource to be sure that a potential repair has been flagged
	@DependsOn(DS_BEAN_NAME)
	public Flyway flyway() throws SQLException {
		ClassicConfiguration conf = new ClassicConfiguration();
		// Old Demyo databases may have schema_version, but that is no longer the default since Flyway 5
		conf.setTable("schema_version");
		conf.setDataSource(dataSource());
		conf.setLocationsAsStrings("org/demyo/model/schema");

		Flyway flyway = new Flyway(conf);
		FlywayUtils.repairIfNeeded(flyway);
		return flyway;
	}

	// Spring data needs this bean to be named entityManagerFactory, even though it's a SessionFactory
	@Bean(name = "entityManagerFactory")
	// Must be run after Flyway to ensure the database is compatible with the code
	// TODO: #205: Maybe there's a way to set everything with Spring Boot here
	@DependsOn(FLYWAY_BEAN_NAME)
	public LocalSessionFactoryBean entityManagerFactory() throws SQLException {
		LocalSessionFactoryBean factory = new LocalSessionFactoryBean();
		factory.setPackagesToScan("org.demyo.model", "org.demyo.model.*", "org.demyo.model.**.*");
		factory.setDataSource(dataSource());

		Properties props = new Properties();
		LOGGER.info("Hibernate debug is enabled: show={}, format={}", showSql, formatSql);
		props.setProperty("hibernate.show_sql", showSql);
		props.setProperty("hibernate.format_sql", formatSql);
		// Dialect will be automatically detected
		// If enabled, Hibernate will collect statistics useful for performance tuning
		props.setProperty("generate_statistics", "false");
		// If turned on, Hibernate will generate comments inside the SQL, for easier debugging
		props.setProperty("hibernate.use_sql_comments", "false");
		factory.setHibernateProperties(props);

		return factory;
	}

	/*
	 * org.springframework.orm.jpa.JpaTransactionManager would also work but using the Hibernate-specific
	 * one allows some specific behaviour:
	 * https://github.com/spring-projects/spring-framework/issues/13599#issuecomment-453364266
	 */
	@Bean("transactionManager")
	public TransactionManager transactionManager() throws SQLException {
		SessionFactory sessionFactory = entityManagerFactory().getObject();
		if (sessionFactory == null) {
			throw new DemyoRuntimeException(DemyoErrorCode.SYS_STARTUP_ERROR, "The session factory is null");
		}
		return new HibernateTransactionManager(sessionFactory);
	}

	@Bean
	public static BeanPostProcessor exceptionTranslator() {
		return new PersistenceExceptionTranslationPostProcessor();
	}

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
}
