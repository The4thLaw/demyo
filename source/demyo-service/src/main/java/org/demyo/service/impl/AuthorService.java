package org.demyo.service.impl;

import org.demyo.dao.IAuthorDao;
import org.demyo.dao.IModelDao;
import org.demyo.model.Author;
import org.demyo.service.IAuthorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implements the contract defined by {@link IAuthorService}.
 * 
 * @author $Author: xr $
 * @version $Revision: 1 $
 */
@Service
public class AuthorService extends AbstractModelService<Author> implements IAuthorService {
	@Autowired
	private IAuthorDao dao;

	/**
	 * Default constructor.
	 */
	public AuthorService() {
		super(Author.class);
	}

	@Override
	protected IModelDao<Author> getDao() {
		return dao;
	}
}
