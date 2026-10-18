package org.demyo.dao;

import org.the4thlaw.common.dao.IDatabaseDao;

/**
 * Provides access to raw SQL operations, for cases when flexibility and reflection primes over clean Hibernate beans.
 */
public interface IRawSQLDao extends IDatabaseDao {
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
