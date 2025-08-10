package org.demyo.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import org.demyo.model.Taxon;

/**
 * Custom implementation of some methods from {@link ITaxonCustomRepo}, to be
 * used as base implementation by Spring Data
 * for {@link ITaxonRepo}.
 */
// Unfortunately, the "I" has to remain as Spring Data expects
// <InterfaceName>Impl. Also, the implementation has
// to be in the same package as the interface.
/* package */class ITaxonRepoImpl implements ITaxonCustomRepo {
	@Autowired
	// Inject "self" so that we can use the findAll method
	private ITaxonRepo repo;

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public List<Taxon> findAllWithUsageCounts() {
		// Find all taxons
		List<Taxon> taxons = repo.findAll(Sort.by("name"));

		// Custom query to get usage counts
		Query query = entityManager
				.createNativeQuery("select taxon_id, count(album_id) from albums_aggregated_taxons group by taxon_id");
		List<?> results = query.getResultList();

		return setUsageCounts(taxons, results);
	}

	private static List<Taxon> setUsageCounts(List<Taxon> taxons, List<?> results) {
		// Create a map for faster lookup
		Map<Long, Integer> occurrences = new HashMap<>();
		for (Object result : results) {
			Object[] row = (Object[]) result;
			long id = ((Number) row[0]).longValue();
			int count = ((Number) row[1]).intValue();
			occurrences.put(id, count);
		}

		// Set all counts one by one
		for (Taxon t : taxons) {
			Integer usageCount = occurrences.get(t.getId());
			if (usageCount == null) {
				usageCount = 0;
			}
			t.setUsageCount(usageCount);
		}

		return taxons;
	}

	@Override
	public List<Taxon> findAllGenresByAuthor(long authorId) {
		Query query = entityManager.createNativeQuery(
				"""
						SELECT
							t.id, count(at.album_id)
						FROM
							albums_authors aa
							INNER JOIN albums_aggregated_taxons at ON aa.album_id = at.album_id
							INNER JOIN taxons t ON t.id = at.taxon_id
						WHERE author_id = ? and t.taxon_type = 'GENRE'
						GROUP BY t.id""");
		query.setParameter(1, authorId);
		List<?> results = query.getResultList();

		List<Long> ids = results.stream().map(row -> ((Number) ((Object[])row)[0]).longValue()).toList();
		List<Taxon> taxons = repo.findAllById(ids, Sort.by("name"));
		return setUsageCounts(taxons, results);
	}
}
