package org.demyo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.demyo.common.exception.DemyoErrorCode;
import org.demyo.common.exception.DemyoRuntimeException;
import org.demyo.dao.IBookTypeRepo;
import org.demyo.dao.IModelRepo;
import org.demyo.dao.IRawSQLDao;
import org.demyo.model.BookType;
import org.demyo.service.ITranslationService;

/**
 * Implements the contract defined by {@link IBookTypeService}.
 */
@Service
public class BookTypeService extends AbstractModelService<BookType> implements
		IBookTypeService {
	@Autowired
	private IBookTypeRepo repo;

	@Autowired
	private IRawSQLDao sqlDao;

	@Autowired
	private ITranslationService translationService;

	/**
	 * Default constructor.
	 */
	public BookTypeService() {
		super(BookType.class);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean isManagementEnabled() {
		if (repo.count() > 1) {
			return true;
		}
		BookType single = repo.findAll().get(0);
		return !BookType.DEFAULT_NAME.equals(single.getName());
	}

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public void enableManagement() {
		if (repo.count() > 1) {
			throw new DemyoRuntimeException(DemyoErrorCode.BOOK_TYPE_MGMT_ALREADY_ENABLED);
		}

		BookType single = repo.findAll().get(0);
		if (!BookType.DEFAULT_NAME.equals(single.getName())) {
			throw new DemyoRuntimeException(DemyoErrorCode.BOOK_TYPE_MGMT_ALREADY_ENABLED);
		}

		single.setName(translationService.translate("special.BookType.initialName"));
		repo.save(single);
	}

	@Transactional(rollbackFor = Throwable.class)
	@Override
	public void reassign(long from, long to) {
		sqlDao.reassignBookTypes(from, to);
	}

	@Override
	protected IModelRepo<BookType> getRepo() {
		return repo;
	}
}
