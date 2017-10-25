package org.demyo.dao;

import org.springframework.stereotype.Repository;

import org.demyo.model.Reader;

/**
 * This class provides methods to manipulate {@link Reader}s.
 */
@Repository
public interface IReaderRepo extends IModelRepo<Reader> {
}
