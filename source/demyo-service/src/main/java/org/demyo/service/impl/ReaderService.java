package org.demyo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.demyo.dao.IModelRepo;
import org.demyo.dao.IReaderRepo;
import org.demyo.model.Reader;
import org.demyo.service.IReaderService;
import org.demyo.service.ISeriesService;

/**
 * Implements the contract defined by {@link ISeriesService}.
 */
@Service
public class ReaderService extends AbstractModelService<Reader> implements IReaderService {
	@Autowired
	private IReaderRepo repo;

	private final ThreadLocal<Reader> currentReader = new ThreadLocal<Reader>() {
		@Override
		protected Reader initialValue() {
			return null;
		}
	};

	/**
	 * Default constructor.
	 */
	public ReaderService() {
		super(Reader.class);
	}

	@Override
	public void setCurrentReader(Reader r) {
		currentReader.set(r);
	}

	@Override
	public Reader getCurrentReader() {
		return currentReader.get();
	}

	@Override
	protected IModelRepo<Reader> getRepo() {
		return repo;
	}

}
