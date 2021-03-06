package org.demyo.dao;

import java.util.List;
import java.util.Map;

/**
 * Provides access to raw SQL operations, for cases when flexibility and reflection primes over clean Hibernate
 * beans.
 */
public interface IRawSQLDao {
	/**
	 * Deletes content from all tables in Demyo.
	 */
	void pruneAllTables();

	/**
	 * Inserts arbitrary data into a table.
	 * 
	 * @param tableName The table into which to insert the data.
	 * @param values A map of column-to-value data to insert.
	 */
	void insert(String tableName, Map<String, ? extends Object> values);

	/**
	 * Counts the number of entries in a table.
	 * 
	 * @param tableName The table to retrieve the number of entries from.
	 * @return The number of entries
	 */
	long count(String tableName);

	/**
	 * Gets the raw data from a table.
	 * 
	 * @param tableName The table to query.
	 * @return The data contained inside the table.
	 */
	List<Map<String, Object>> getRawRecords(String tableName);

	/**
	 * Gets the version of the Demyo schema.
	 * 
	 * @return The schema version.
	 */
	int getSchemaVersion();
}
