package org.demyo.dao;

import org.demyo.model.Album;
import org.demyo.model.MetaSeries;

import org.springframework.stereotype.Repository;

/**
 * This class provides methods to manipulate lists of {@link Album}s.
 */
@Repository
public interface IMetaSeriesRepo extends IModelRepo<MetaSeries> {
}
