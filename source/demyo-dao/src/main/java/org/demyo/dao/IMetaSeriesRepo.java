package org.demyo.dao;

import org.demyo.model.Album;
import org.demyo.model.MetaSeries;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

/**
 * This class provides methods to manipulate lists of {@link Album}s.
 */
@Repository
public interface IMetaSeriesRepo extends IModelRepo<MetaSeries> {

	@Override
	@EntityGraph("MetaSeries.forIndex")
	Slice<MetaSeries> findAll(Pageable pageable);
}
