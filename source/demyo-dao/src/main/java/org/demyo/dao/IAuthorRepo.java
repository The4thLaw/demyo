package org.demyo.dao;

import org.demyo.model.Author;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * This class provides methods to manipulate {@link Author}s.
 */
@Repository
public interface IAuthorRepo extends IModelRepo<Author> {
	@Override
	@Query("select x from #{#entityName} x where id=?1")
	@EntityGraph("Author.forEdition")
	public Author findOneForEdition(long id);

	@Query("select x from #{#entityName} x where id=?1")
	@EntityGraph("Author.forView")
	public Author findOneForView(long id);
}
