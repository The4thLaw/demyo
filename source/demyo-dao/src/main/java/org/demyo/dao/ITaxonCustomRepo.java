package org.demyo.dao;

import java.util.List;

import org.demyo.model.Taxon;

/**
 * Custom methods to supplement the default offering of Spring Data.
 */
/*package*/interface ITaxonCustomRepo {
	/**
	 * Finds all Taxons, along with the number of times the taxon has been used.
	 *
	 * @return The taxon list
	 */
	List<Taxon> findAllWithUsageCounts();

	/**
	 * Finds all Taxons an Author has worked on in at least one album, along with the number
	 * of albums the taxon has been used in for that author.
	 */
	List<Taxon> findAllGenresByAuthor(long authorId);
}
