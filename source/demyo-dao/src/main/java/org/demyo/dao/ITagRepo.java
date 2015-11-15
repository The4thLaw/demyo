package org.demyo.dao;

import org.demyo.model.Tag;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * This class provides methods to manipulate {@link Tag}s.
 */
@Repository
public interface ITagRepo extends IModelRepo<Tag>, ITagCustomRepo {
	@Query("select x from #{#entityName} x where id=?1")
	@EntityGraph("Tag.forView")
	public Tag findOneForView(long id);
}
