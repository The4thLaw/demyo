package org.demyo.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

import org.demyo.dao.IConfigurationEntryRepo;
import org.demyo.model.ConfigurationEntry;
import org.demyo.model.config.ApplicationConfiguration;
import org.demyo.service.IConfigurationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implements the contract defined by {@link IConfigurationService}.
 */
@Service
public class ConfigurationService implements IConfigurationService {
	@Autowired
	private IConfigurationEntryRepo repo;

	private ApplicationConfiguration config = null;

	/**
	 * Reloads the configuration from the source.
	 */
	@PostConstruct
	@Transactional(readOnly = true)
	public void reloadConfiguration() {
		Map<String, String> configurationValues = new HashMap<>();
		for (ConfigurationEntry entry : repo.findAll()) {
			configurationValues.put(entry.getKey(), entry.getValue());
		}
		config = new ApplicationConfiguration(configurationValues);
	}

	@Override
	public ApplicationConfiguration getConfiguration() {
		return config;
	}

	@Override
	@Transactional
	public void save(@NotNull ApplicationConfiguration newConfig) {
		save(newConfig.asMap());
	}

	@Override
	@Transactional
	public void save(Map<String, String> newConfig) {
		// Merge what is in the database with what has been provided

		// Load from DB and index
		Map<String, ConfigurationEntry> configurationValues = new HashMap<>();
		for (ConfigurationEntry entry : repo.findAll()) {
			configurationValues.put(entry.getKey(), entry);
		}

		// Copy from new configuration to entities
		for (Entry<String, String> entry : newConfig.entrySet()) {
			configurationValues.get(entry.getKey()).setValue(entry.getValue());
		}

		// Now save everything
		repo.save(configurationValues.values());

		// And reload a fresh one
		reloadConfiguration();
	}
}
