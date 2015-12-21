package org.demyo.dao;

import java.util.List;

import org.demyo.model.Series;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * This class provides methods to manipulate {@link Series}.
 */
@Repository
public interface ISeriesRepo extends IModelRepo<Series>, ISeriesCustomRepo {
	@Query("select x from #{#entityName} x where id=?1")
	@EntityGraph("Series.forView")
	public Series findOneForView(long id);

	@Override
	@Query("select x from #{#entityName} x where id=?1")
	@EntityGraph("Series.forEdition")
	public Series findOneForEdition(long id);

	public List<Series> findByIdNot(long id);
}
