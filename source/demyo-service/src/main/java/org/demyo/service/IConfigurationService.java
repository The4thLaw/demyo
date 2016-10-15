package org.demyo.service;

import java.util.Map;

import org.demyo.model.config.ApplicationConfiguration;

/**
 * Service to manage the {@link ApplicationConfiguration}. To get instances of
 * {@link org.demyo.common.config.SystemConfiguration SystemConfiguration}, see instead
 * {@link org.demyo.common.config.SystemConfiguration#getInstance() SystemConfiguration.getInstance()}.
 * <p>
 * Avoid changing values in the configuration, as it might be shared with others parts of the application
 * immediately.
 * </p>
 */
public interface IConfigurationService {

	/**
	 * Gets the application configuration. It is cached, so calls to this method are inexpensive.
	 * 
	 * 
	 * @return The application configuration.
	 */
	ApplicationConfiguration getConfiguration();

	/**
	 * Saves the provided values to the store.
	 * 
	 * @param config The configuration values to save.
	 */
	void save(ApplicationConfiguration config);

	/**
	 * Saves the provided values to the store. If a value is missing from the provided map, its current value is
	 * preserved.
	 * 
	 * @param config The configuration values to save.
	 */
	void save(Map<String, String> config);
}
