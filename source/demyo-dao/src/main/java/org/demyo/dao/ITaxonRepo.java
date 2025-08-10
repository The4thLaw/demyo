package org.demyo.dao;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.demyo.model.Taxon;

/**
 * This class provides methods to manipulate {@link Taxon}s.
 */
@Repository
public interface ITaxonRepo extends IModelRepo<Taxon>, IQuickSearchableRepo<Taxon>, ITaxonCustomRepo {
	@Override
	@Query("select x from #{#entityName} x where name=?1 order by name")
	List<Taxon> quickSearchExact(String query);

	@Override
	@Query("select x from #{#entityName} x where name like ?1 order by name")
	List<Taxon> quickSearchLike(String query);

	List<Taxon> findAllById(Iterable<Long> ids, Sort sort);
}
