package org.demyo.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import org.demyo.model.Tag;

/**
 * Custom implementation of some methods from {@link ITagCustomRepo}, to be used as base implementation by Spring Data
 * for {@link ITagRepo}.
 */
// Unfortunately, the "I" has to remain as Spring Data expects <InterfaceName>Impl. Also, the implementation has
// to be in the same package as the interface.
/*package*/class ITagRepoImpl implements ITagCustomRepo {
	@Autowired
	// Inject "self" so that we can use the findAll method
	private ITagRepo repo;

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public List<Tag> findAllWithUsageCounts() {
		// Find all tags
		List<Tag> tags = repo.findAll(Sort.by("name"));

		// Custom query to get usage counts
		Query query = entityManager
				.createNativeQuery("select tag_id, count(album_id) from albums_tags group by tag_id");
		List<?> results = query.getResultList();

		// Create a map for faster lookup
		Map<Long, Integer> occurrences = new HashMap<>();
		for (Object result : results) {
			Object[] row = (Object[]) result;
			long id = ((Number) row[0]).longValue();
			int count = ((Number) row[1]).intValue();
			occurrences.put(id, count);
		}

		// Set all counts one by one
		for (Tag t : tags) {
			Integer usageCount = occurrences.get(t.getId());
			if (usageCount == null) {
				usageCount = 0;
			}
			t.setUsageCount(usageCount);
		}

		return tags;
	}

}
