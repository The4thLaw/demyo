package org.demyo.dao;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.sql.JoinType;

/**
 * Wrapper class to set fetch modes for use with {@link Criteria#setFetchMode(String, FetchMode)}.
 * 
 * @author $Author: xr $
 * @version $Revision: 1063 $
 */
public class FetchModeHolder {
	private Map<String, JoinType> fetchModes = new HashMap<>();

	// Use default constructor

	/**
	 * Adds an association fetching strategy for an association or a collection of values.
	 * 
	 * @param associationPath a dot seperated property path
	 * @param mode The fetch mode for the referenced association
	 * @return The current object, for chaining.
	 * @see Criteria#setFetchMode(String, FetchMode)
	 */
	public FetchModeHolder add(String associationPath, JoinType mode) {
		fetchModes.put(associationPath, mode);
		return this;
	}

	/**
	 * Sets all known association strategies into the given criteria.
	 * 
	 * @param criteria The criteria to populate.
	 */
	public void populateCriteria(Criteria criteria) {
		for (Entry<String, JoinType> entry : fetchModes.entrySet()) {
			//criteria.setFetchMode(entry.getKey(), entry.getValue());

			//if (FetchMode.JOIN.equals(entry.getValue())) {
			//criteria.createAlias(entry.getKey(), entry.getKey());
			criteria.createAlias(entry.getKey(), entry.getKey(), entry.getValue());
			//}
		}
	}
}
