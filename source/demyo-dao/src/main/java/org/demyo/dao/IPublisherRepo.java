package org.demyo.dao;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.demyo.model.Publisher;

/**
 * This class provides methods to manipulate {@link Publisher}s.
 */
@Repository
public interface IPublisherRepo extends IModelRepo<Publisher>, IQuickSearchableRepo<Publisher>,
		IPublisherCustomRepo {
	@Query("select x from Publisher x")
	@EntityGraph("Publisher.forIndex")
	List<Publisher> findAllForIndex(Sort sort);

	@Query("select x from #{#entityName} x where id=?1")
	@EntityGraph("Publisher.forView")
	Publisher findOneForView(long id);

	@Override
	@Query("select x from #{#entityName} x where id=?1")
	@EntityGraph("Publisher.forEdition")
	Publisher findOneForEdition(long id);

	@Override
	@Query("select x from #{#entityName} x where name=?1 order by name")
	List<Publisher> quickSearchExact(String query);

	@Override
	@Query("select x from #{#entityName} x where name like ?1 order by name")
	List<Publisher> quickSearchLike(String query);
}
