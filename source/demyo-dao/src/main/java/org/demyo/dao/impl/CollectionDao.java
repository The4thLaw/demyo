package org.demyo.dao.impl;

import org.demyo.dao.ICollectionDao;
import org.demyo.model.Collection;

import org.springframework.stereotype.Repository;

/**
 * Implements the contract defined by {@link ICollectionDao}.
 * 
 * @author $Author: xr $
 * @version $Revision: 1084 $
 */
@Repository
public class CollectionDao extends AbstractModelDao<Collection> implements ICollectionDao {

	/**
	 * Default constructor.
	 */
	public CollectionDao() {
		super(Collection.class);
	}
}
