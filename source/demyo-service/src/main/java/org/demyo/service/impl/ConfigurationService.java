package org.demyo.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.validation.constraints.NotNull;

import org.demyo.common.config.SystemConfiguration;
import org.demyo.common.exception.DemyoErrorCode;
import org.demyo.common.exception.DemyoException;
import org.demyo.common.exception.DemyoRuntimeException;
import org.demyo.model.config.ApplicationConfiguration;
import org.demyo.service.IConfigurationService;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

/**
 * Implements the contract defined by {@link IConfigurationService}.
 */
@Service
public class ConfigurationService implements IConfigurationService {
	private ApplicationConfiguration config = null;

	@Override
	public synchronized ApplicationConfiguration getConfiguration() {
		if (config != null) {
			return config;
		}

		config = new ApplicationConfiguration(getFromDisk());
		return config;
	}

	@Override
	public synchronized void save(@NotNull ApplicationConfiguration newConfig) throws DemyoException {
		PropertiesConfiguration configProps = getFromDisk(); // Load from disk to keep the layout
		newConfig.fillConfiguration(configProps);
		try {
			configProps.save();
		} catch (ConfigurationException e) {
			throw new DemyoException(DemyoErrorCode.CONFIG_CANNOT_SAVE, e);
		}
		this.config = null; // Reload from the file rather than what is passed
	}

	private PropertiesConfiguration getFromDisk() {
		File configFile = SystemConfiguration.getInstance().getConfigurationFile();

		if (!configFile.exists()) {
			// The file does not exist. Create it from a template.
			try (InputStream configTemplateIs = ConfigurationService.class
					.getResourceAsStream("/org/demyo/model/config/demyo-config.properties")) {
				if (configTemplateIs == null) {
					throw new DemyoRuntimeException(DemyoErrorCode.CONFIG_TEMPLATE_NOT_FOUND);
				}
				FileUtils.copyInputStreamToFile(configTemplateIs, configFile);

			} catch (IOException e) {
				throw new DemyoRuntimeException(DemyoErrorCode.CONFIG_TEMPLATE_CANNOT_COPY, e);
			}
		}

		try {
			return new PropertiesConfiguration(configFile);
		} catch (ConfigurationException e) {
			throw new DemyoRuntimeException(DemyoErrorCode.CONFIG_CANNOT_LOAD, e);
		}
	}
}
