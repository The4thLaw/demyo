package org.demyo.dao;

import java.util.List;

import org.demyo.model.Tag;

/**
 * This class provides methods to CRUD {@link Tag}s.
 */
@Deprecated
public interface ITagDao extends IModelDao<Tag> {

	/** Finds all Tags, along with the number of */
	List<Tag> findAllTagsWithUsageCount();
}
