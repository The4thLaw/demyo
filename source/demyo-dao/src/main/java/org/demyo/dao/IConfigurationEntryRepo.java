package org.demyo.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import org.demyo.model.ConfigurationEntry;

/**
 * This class provides methods to manipulate {@link ConfigurationEntry}s.
 */
@Repository
public interface IConfigurationEntryRepo extends IModelRepo<ConfigurationEntry> {
	/**
	 * Finds all entries for the given Reader.
	 * 
	 * @param id The internal identifier of the Reader.
	 * @return The configuration entries.
	 */
	List<ConfigurationEntry> findAllByReaderId(long id);
}
