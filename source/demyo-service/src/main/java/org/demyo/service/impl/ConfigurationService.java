package org.demyo.service.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.apache.commons.collections.CollectionUtils;
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
		Set<ConfigurationEntry> saved = save(ApplicationConfiguration.getDefaultConfiguration(), reader);
		reader.setConfigurationEntries(saved);
	}

	@Override
	@Transactional
	public Set<ConfigurationEntry> save(@NotNull ApplicationConfiguration newConfig, Reader reader) {
		return save(newConfig.asMap(), reader);
	}

	@Override
	@Transactional
	public Set<ConfigurationEntry> save(Map<String, String> newConfig, Reader reader) {
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
		Iterable<ConfigurationEntry> saved = repo.save(configurationValues.values());
		Set<ConfigurationEntry> savedSet = new HashSet<>();
		CollectionUtils.addAll(savedSet, saved.iterator());
		return savedSet;
	}

	@Override
	public Locale getLocaleForFirstReader() {
		ConfigurationEntry entry = repo.findFirstByKeyOrderByReaderId(ApplicationConfiguration.CONFIG_KEY_LANGUAGE);
		if (entry == null) {
			return null;
		}
		LOGGER.trace("Locale for first reader is {}", entry.getValue());
		return Locale.forLanguageTag(entry.getValue());
	}
}
