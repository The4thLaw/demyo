package org.demyo.dao;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.demyo.model.Series;

/**
 * This class provides methods to manipulate {@link Series}.
 */
@Repository
public interface ISeriesRepo extends IModelRepo<Series>, IQuickSearchableRepo<Series> {
	@Query("select x from #{#entityName} x where id=?1")
	@EntityGraph("Series.forView")
	Series findOneForView(long id);

	@Override
	@Query("select x from #{#entityName} x where id=?1")
	@EntityGraph("Series.forView")
	Series findOneForEdition(long id);

	List<Series> findByIdNot(long id);

	@Override
	@Query("select x from #{#entityName} x where name=?1 order by name")
	List<Series> quickSearchExact(String query);

	@Override
	@Query("select x from #{#entityName} x where name like ?1 order by name")
	List<Series> quickSearchLike(String query);
}
