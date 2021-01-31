package org.demyo.test.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.dbunit.ext.h2.H2DataTypeFactory;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.SimpleThreadScope;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.github.springtestdbunit.bean.DatabaseConfigBean;
import com.github.springtestdbunit.bean.DatabaseDataSourceConnectionFactoryBean;

/**
 * Standard test configuration for Demyo.
 */
@Configuration
public class TestConfiguration {
	// Thanks to https://stackoverflow.com/a/8803166/109813
	@Bean
	public CustomScopeConfigurer customScope() {
		CustomScopeConfigurer scopeConf = new CustomScopeConfigurer();
		Map<String, Object> scopes = new HashMap<>();
		scopes.put("session", new SimpleThreadScope());
		scopes.put("request", new SimpleThreadScope());
		scopeConf.setScopes(scopes);
		return scopeConf;
	}

	@Bean
	public DataSource dataSource() {
		return new DriverManagerDataSource("jdbc:h2:mem:demyo_tests;DB_CLOSE_DELAY=120;IGNORECASE=TRUE");
	}

	@Bean
	public DatabaseConfigBean dbUnitDatabaseConfig() {
		DatabaseConfigBean dbConfig = new DatabaseConfigBean();
		dbConfig.setDatatypeFactory(new H2DataTypeFactory());
		return dbConfig;
	}

	@Bean
	public DatabaseDataSourceConnectionFactoryBean dbUnitDatabaseConnection() {
		DatabaseDataSourceConnectionFactoryBean dbConnection = new DatabaseDataSourceConnectionFactoryBean(
				dataSource());
		dbConnection.setDatabaseConfig(dbUnitDatabaseConfig());
		return dbConnection;
	}
}
