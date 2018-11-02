package org.demyo.service;

import java.util.Map;
import java.util.Set;

import org.demyo.model.ConfigurationEntry;
import org.demyo.model.Reader;
import org.demyo.model.config.ApplicationConfiguration;

/**
 * Service to manage the {@link ApplicationConfiguration}. To get instances of
 * {@link org.demyo.common.config.SystemConfiguration SystemConfiguration}, see instead
 * {@link org.demyo.common.config.SystemConfiguration#getInstance() SystemConfiguration.getInstance()}.
 * <p>
 * Avoid changing values in the configuration, as it might be shared with others parts of the application immediately.
 * </p>
 */
public interface IConfigurationService {
	/**
	 * Creates a default configuration for the provided Reader (the Reader must exist in the database already).
	 * <p>
	 * The saved configuration is put in the Reader so that it can directly be used without reloading the bean outside
	 * of the session.
	 * </p>
	 * 
	 * @param reader The {@link Reader} to whom the configuration applies.
	 */
	void createDefaultConfiguration(Reader reader);

	/**
	 * Saves the provided values to the store.
	 * 
	 * @param config The configuration values to save.
	 * @param reader The {@link Reader} to whom the configuration applies.
	 * @return The saved entries.
	 */
	Set<ConfigurationEntry> save(ApplicationConfiguration config, Reader reader);

	/**
	 * Saves the provided values to the store. If a value is missing from the provided map, its current value is
	 * preserved.
	 * 
	 * @param config The configuration values to save.
	 * @param reader The {@link Reader} to whom the configuration applies.
	 * @return The saved entries.
	 */
	Set<ConfigurationEntry> save(Map<String, String> config, Reader reader);
}
