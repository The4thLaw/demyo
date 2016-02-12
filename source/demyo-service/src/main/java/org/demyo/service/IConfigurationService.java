package org.demyo.service;

import org.demyo.common.exception.DemyoException;
import org.demyo.model.config.ApplicationConfiguration;

/**
 * Service to manage the {@link ApplicationConfiguration}. To get instances of
 * {@link org.demyo.common.config.SystemConfiguration SystemConfiguration}, see instead
 * {@link org.demyo.common.config.SystemConfiguration#getInstance() SystemConfiguration.getInstance()}.
 * <p>
 * Avoid changing values in the configuration, as it might be shared with others parts of the application
 * immediately.
 * </p>
 * <p>
 * The location of the configuration file is defined by the {@link org.demyo.common.config.SystemConfiguration
 * SystemConfiguration}.
 * </p>
 */
public interface IConfigurationService {

	/**
	 * Gets the application configuration. If the configuration is not yet known by the application, it is loaded
	 * from the file system. If there is no such file on the file system, it is created with default values. If the
	 * configuration was loaded recently, the known instance is returned.
	 * 
	 * 
	 * @return The application configuration.
	 */
	ApplicationConfiguration getConfiguration();

	/**
	 * Saves the current values to disk. Note that the saved values are the ones from the passed argument, and not
	 * the potentially cached ones.
	 * 
	 * @param config The configuration values to save.
	 * @throws DemyoException if the configuration cannot be saved.
	 */
	void save(ApplicationConfiguration config) throws DemyoException;
}
