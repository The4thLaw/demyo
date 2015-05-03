package org.demyo.service.impl;

import org.demyo.dao.ICollectionDao;
import org.demyo.dao.IModelDao;
import org.demyo.model.Collection;
import org.demyo.service.ICollectionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implements the contract defined by {@link ICollectionService}.
 * 
 * @author $Author: xr $
 * @version $Revision: 1084 $
 */
@Service
public class CollectionService extends AbstractModelService<Collection> implements ICollectionService {
	@Autowired
	private ICollectionDao dao;

	/**
	 * Default constructor.
	 */
	public CollectionService() {
		super(Collection.class);
	}

	@Override
	protected IModelDao<Collection> getDao() {
		return dao;
	}
}
