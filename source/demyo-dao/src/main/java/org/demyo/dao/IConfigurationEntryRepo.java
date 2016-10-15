package org.demyo.dao;

import org.demyo.model.ConfigurationEntry;

import org.springframework.stereotype.Repository;

/**
 * This class provides methods to manipulate {@link ConfigurationEntry}s.
 */
@Repository
public interface IConfigurationEntryRepo extends IModelRepo<ConfigurationEntry> {

}
