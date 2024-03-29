package org.demyo.service.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.validation.constraints.NotNull;

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
	@Transactional(readOnly = true)
	public Map<String, String> getGlobalConfiguration() {
		Map<String, ConfigurationEntry> configurationValues = new HashMap<>();
		loadConfiguration(configurationValues, null);
		return configurationValues.entrySet().stream()
				// Can't insert null in the target map, which can happen when saving empty values
				// after a migration
				.filter(e -> e.getValue().getValue() != null)
				// Generate the target map
				.collect(Collectors.toMap(
						Map.Entry::getKey, // Preserve key
						entry -> entry.getValue().getValue() // Map value
				));
	}

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
		// Load the globals
		loadConfiguration(configurationValues, null);
		// Then the reader
		loadConfiguration(configurationValues, reader.getId());

		// Copy from new configuration to entities
		for (Entry<String, String> entry : newConfig.entrySet()) {
			if (configurationValues.containsKey(entry.getKey())) {
				configurationValues.get(entry.getKey()).setValue(entry.getValue());
			} else {
				ConfigurationEntry newEntry = new ConfigurationEntry();
				newEntry.setKey(entry.getKey());
				newEntry.setValue(entry.getValue());
				if (!ApplicationConfiguration.isGlobalEntry(entry.getKey())) {
					newEntry.setReader(reader);
				}
				configurationValues.put(entry.getKey(), newEntry);
			}
		}

		// Now save everything
		Iterable<ConfigurationEntry> saved = repo.saveAll(configurationValues.values());
		Set<ConfigurationEntry> savedSet = new HashSet<>();
		CollectionUtils.addAll(savedSet, saved.iterator());
		return savedSet;
	}

	private void loadConfiguration(Map<String, ConfigurationEntry> configurationValues, Long readerId) {
		for (ConfigurationEntry entry : repo.findAllByReaderId(readerId)) {
			configurationValues.put(entry.getKey(), entry);
		}
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
