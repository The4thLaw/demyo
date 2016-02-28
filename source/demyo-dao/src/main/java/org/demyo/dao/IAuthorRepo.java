package org.demyo.dao;

import java.util.List;

import org.demyo.model.Author;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * This class provides methods to manipulate {@link Author}s.
 */
@Repository
public interface IAuthorRepo extends IModelRepo<Author>, IQuickSearchableRepo<Author> {
	@Override
	@Query("select x from #{#entityName} x where id=?1")
	@EntityGraph("Author.forEdition")
	Author findOneForEdition(long id);

	@Query("select x from #{#entityName} x where id=?1")
	@EntityGraph("Author.forView")
	Author findOneForView(long id);

	@Override
	@Query("select x from #{#entityName} x where name=?1 or firstName=?1 or nickname=?1 "
			+ "order by name, firstName, nickname")
	List<Author> quickSearchExact(String query);

	@Override
	@Query("select x from #{#entityName} x where name like ?1 or firstName like ?1 or nickname like ?1 "
			+ "order by name, firstName, nickname")
	List<Author> quickSearchLike(String query);
}
