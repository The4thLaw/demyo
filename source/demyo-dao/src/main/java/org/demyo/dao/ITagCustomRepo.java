package org.demyo.dao;

import java.util.List;

import org.demyo.model.Tag;

/**
 * Custom methods to supplement the default offering of Spring Data.
 */
/*package*/interface ITagCustomRepo {
	/** Finds all Tags, along with the number of times the tag has been used */
	List<Tag> findAllWithUsageCounts();
}
