package org.demyo.dao;

import org.springframework.stereotype.Repository;

import org.demyo.model.BookType;

/**
 * This class provides methods to manipulate {@link BookType}s.
 */
@Repository
public interface IBookTypeRepo extends IModelRepo<BookType> {
}
