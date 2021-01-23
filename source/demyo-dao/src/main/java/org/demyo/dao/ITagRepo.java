package org.demyo.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.demyo.model.Tag;

/**
 * This class provides methods to manipulate {@link Tag}s.
 */
@Repository
public interface ITagRepo extends IModelRepo<Tag>, IQuickSearchableRepo<Tag>, ITagCustomRepo {
	@Override
	@Query("select x from #{#entityName} x where name=?1 order by name")
	List<Tag> quickSearchExact(String query);

	@Override
	@Query("select x from #{#entityName} x where name like ?1 order by name")
	List<Tag> quickSearchLike(String query);
}
