package org.demyo.dao;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.demyo.model.Author;

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
	@EntityGraph("Author.forList")
	@Query("select a from Author a where name=?1 or firstName=?1 or nickname=?1 "
			+ "order by name, firstName, nickname")
	List<Author> quickSearchExact(String query);

	@Override
	@EntityGraph("Author.forList")
	@Query("select a from Author a where name like ?1 or firstName like ?1 or nickname like ?1 "
			+ "order by name, firstName, nickname")
	List<Author> quickSearchLike(String query);

	/**
	 * Finds all "real" authors, i.e. authors who are not a pseudonym of another.
	 *
	 * @param sort The order.
	 * @return a list of authors
	 */
	@Query("select a from Author a where a.pseudonymOf is null")
	List<Author> findAllReal(Sort sort);

	/**
	 * Finds all authors, including the reference to the main one if relevant.
	 *
	 * @return a list of authors.
	 */
	@Query("select a from Author a")
	@EntityGraph("Author.forList")
	List<Author> findAllWithPseudonyms();
}
