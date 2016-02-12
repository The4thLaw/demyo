package org.demyo.dao;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.sql.JoinType;

/**
 * Wrapper class to set fetch modes for use with {@link Criteria#createAlias(String, String, JoinType)}. By
 * convention, the association is aliased to its name.
 */
public class JoinTypeHolder {
	private Map<String, JoinType> joinTypes = new HashMap<>();

	// Use default constructor

	/**
	 * Adds an association fetching strategy for an association or a collection of values.
	 * 
	 * @param associationPath a dot seperated property path
	 * @param type The join type for the referenced association
	 * @return The current object, for chaining.
	 * @see Criteria#setFetchMode(String, FetchMode)
	 */
	public JoinTypeHolder add(String associationPath, JoinType type) {
		joinTypes.put(associationPath, type);
		return this;
	}

	/**
	 * Sets all known association strategies into the given criteria.
	 * 
	 * @param criteria The criteria to populate.
	 */
	public void populateCriteria(Criteria criteria) {
		for (Entry<String, JoinType> entry : joinTypes.entrySet()) {
			criteria.createAlias(entry.getKey(), entry.getKey(), entry.getValue());
		}
	}
}
