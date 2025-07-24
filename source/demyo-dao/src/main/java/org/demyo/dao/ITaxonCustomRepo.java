package org.demyo.dao;

import java.util.List;

import org.demyo.model.Taxon;

/**
 * Custom methods to supplement the default offering of Spring Data.
 */
/*package*/interface ITaxonCustomRepo {
	/**
	 * Finds all Tags, along with the number of times the tag has been used.
	 *
	 * @return The tag list
	 */
	List<Taxon> findAllWithUsageCounts();
}
