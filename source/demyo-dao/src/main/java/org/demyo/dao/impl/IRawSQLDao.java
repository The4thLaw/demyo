package org.demyo.dao.impl;

import java.util.Map;

/**
 * Provides access to raw SQL operations, for cases when flexibility and reflection primes over clean Hibernate
 * beans.
 * 
 * @author $Author: xr $
 * @version $Revision: 1054 $
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
}
