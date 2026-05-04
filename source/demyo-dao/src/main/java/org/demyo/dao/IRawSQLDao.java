package org.demyo.dao;

import java.util.List;
import java.util.Map;

/**
 * Provides access to raw SQL operations, for cases when flexibility and reflection primes over clean Hibernate beans.
 */
public interface IRawSQLDao {
	/**
	 * Deletes content from all tables in Demyo.
	 */
	void pruneAllTables();

	/**
	 * Fix the values of the identity columns, for example after an import or migration.
	 * <p>
	 * Due to a change in H2 2.x, the behaviour of identity columns has changed (see
	 * https://github.com/h2database/h2database/issues/3454). As such, we need to manually fix the values of the
	 * identity columns when we assign values of our own.
	 * </p>
	 */
	void fixAutoIncrements();

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

	/**
	 * Reassigns any entry using one book type to another.
	 *
	 * @param from The old book type
	 * @param to The new book type
	 */
	void reassignBookTypes(long from, long to);

	/**
	 * Sets an author as a pseudonym of another.
	 * @param authorId The author to edit.
	 * @param pseudonymOfId The author of which the edited one is a pseudonym.
	 */
    void setAuthorPseudonym(String authorId, String pseudonymOfId);
}
