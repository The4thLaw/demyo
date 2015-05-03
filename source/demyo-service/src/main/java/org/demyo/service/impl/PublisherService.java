package org.demyo.service.impl;

import java.util.List;

import org.demyo.dao.FetchModeHolder;
import org.demyo.dao.IModelDao;
import org.demyo.dao.IPublisherDao;
import org.demyo.model.Publisher;
import org.demyo.service.IPublisherService;

import org.hibernate.FetchMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implements the contract defined by {@link IPublisherService}.
 * 
 * @author $Author: xr $
 * @version $Revision: 1084 $
 */
@Service
public class PublisherService extends AbstractModelService<Publisher> implements IPublisherService {
	@Autowired
	private IPublisherDao dao;

	/**
	 * Default constructor.
	 */
	public PublisherService() {
		super(Publisher.class);
	}

	@Override
	protected IModelDao<Publisher> getDao() {
		return dao;
	}

	@Transactional(readOnly = true)
	@Override
	public List<Publisher> findPaginated(int currentPage) {
		FetchModeHolder fetchModes = new FetchModeHolder();
		fetchModes.add("collections", FetchMode.JOIN);
		// TODO: probably going to need to order by two columns: Publisher name and Collection name
		return findPaginated(currentPage, null, fetchModes);
	}
}
