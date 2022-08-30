package org.demyo.dao.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.ClassicConfiguration;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.config.BootstrapMode;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import org.demyo.common.exception.DemyoErrorCode;
import org.demyo.common.exception.DemyoRuntimeException;
import org.demyo.dao.utils.FlywayUtils;

/**
 * Configuration for the DAO layer.
 */
@Configuration
@ComponentScan("org.demyo")
@PropertySource("classpath:/org/demyo/dao/config.properties")
@EnableJpaRepositories(basePackages = "org.demyo.dao", bootstrapMode = BootstrapMode.DEFERRED)
@EnableTransactionManagement
public class DaoConfig {
	private static final Logger LOGGER = LoggerFactory.getLogger(DaoConfig.class);
	private static final String FLYWAY_BEAN_NAME = "flyway";

	@Value("${config.hibernate.debug}")
	private String hibernateDebug;

	public DaoConfig() {
		LOGGER.info("Parsing configuration");
	}

	@Bean(name = "dataSource")
	public DataSource dataSource() {
		JndiDataSourceLookup lookup = new JndiDataSourceLookup();
		return lookup.getDataSource("org.demyo.services.dataSource");
	}

	@Bean(name = FLYWAY_BEAN_NAME, initMethod = "migrate")
	public Flyway flyway() {
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
	@DependsOn(FLYWAY_BEAN_NAME)
	public LocalSessionFactoryBean entityManagerFactory() {
		LocalSessionFactoryBean factory = new LocalSessionFactoryBean();
		factory.setPackagesToScan("org.demyo.model", "org.demyo.model.*", "org.demyo.model.**.*");
		factory.setDataSource(dataSource());

		Properties props = new Properties();
		LOGGER.info("Hibernate debug is enabled: {}", hibernateDebug);
		props.setProperty("hibernate.show_sql", hibernateDebug);
		props.setProperty("hibernate.format_sql", hibernateDebug);
		props.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
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
	public TransactionManager transactionManager() {
		SessionFactory sessionFactory = entityManagerFactory().getObject();
		if (sessionFactory == null) {
			throw new DemyoRuntimeException(DemyoErrorCode.SYS_STARTUP_ERROR, "The session factory is null");
		}
		return new HibernateTransactionManager(sessionFactory);
	}

	@Bean
	public BeanPostProcessor exceptionTranslator() {
		return new PersistenceExceptionTranslationPostProcessor();
	}

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
}
