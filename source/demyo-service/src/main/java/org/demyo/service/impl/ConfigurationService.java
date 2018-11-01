package org.demyo.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.demyo.dao.IConfigurationEntryRepo;
import org.demyo.model.ConfigurationEntry;
import org.demyo.model.Reader;
import org.demyo.model.config.ApplicationConfiguration;
import org.demyo.service.IConfigurationService;

/**
 * Implements the contract defined by {@link IConfigurationService}.
 */
@Service
public class ConfigurationService implements IConfigurationService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationService.class);

	@Autowired
	private IConfigurationEntryRepo repo;

	@Override
	@Transactional
	public void createDefaultConfiguration(Reader reader) {
		save(ApplicationConfiguration.getDefaultConfiguration(), reader);
	}

	@Override
	@Transactional
	public void save(@NotNull ApplicationConfiguration newConfig, Reader reader) {
		save(newConfig.asMap(), reader);
	}

	@Override
	@Transactional
	public void save(Map<String, String> newConfig, Reader reader) {
		LOGGER.debug("Saving configuration for reader #{}", reader.getId());

		// Merge what is in the database with what has been provided

		// Load from DB and index
		Map<String, ConfigurationEntry> configurationValues = new HashMap<>();
		for (ConfigurationEntry entry : repo.findAllByReaderId(reader.getId())) {
			configurationValues.put(entry.getKey(), entry);
		}

		// Copy from new configuration to entities
		for (Entry<String, String> entry : newConfig.entrySet()) {
			if (configurationValues.containsKey(entry.getKey())) {
				configurationValues.get(entry.getKey()).setValue(entry.getValue());
			} else {
				ConfigurationEntry newEntry = new ConfigurationEntry();
				newEntry.setKey(entry.getKey());
				newEntry.setValue(entry.getValue());
				newEntry.setReader(reader);
				configurationValues.put(entry.getKey(), newEntry);
			}
		}

		// Now save everything
		repo.save(configurationValues.values());
	}
}
