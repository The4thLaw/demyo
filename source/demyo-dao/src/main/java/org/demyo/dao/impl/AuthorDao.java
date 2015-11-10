package org.demyo.dao.impl;

import org.demyo.dao.IAuthorDao;
import org.demyo.model.Author;

import org.springframework.stereotype.Repository;

/**
 * Implements the contract defined by {@link IAuthorDao}.
 * 
 * @author $Author: xr $
 * @version $Revision: 1 $
 */
@Repository
@Deprecated
public class AuthorDao extends AbstractModelDao<Author> implements IAuthorDao {

	/**
	 * Default constructor.
	 */
	public AuthorDao() {
		super(Author.class);
	}
}
