package org.demyo.dao.impl;

import org.demyo.dao.IPublisherDao;
import org.demyo.model.Publisher;

import org.springframework.stereotype.Repository;

/**
 * Implements the contract defined by {@link IPublisherDao}.
 * 
 * @author $Author: xr $
 * @version $Revision: 1082 $
 */
@Repository
public class PublisherDao extends AbstractModelDao<Publisher> implements IPublisherDao {

	/**
	 * Default constructor.
	 */
	public PublisherDao() {
		super(Publisher.class);
	}
}
