package org.demyo.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.demyo.model.Series;

/**
 * Implements the contract defined by {@link ISeriesCustomRepo}.
 */
/* package */class ISeriesRepoImpl implements ISeriesCustomRepo {
	private static final Logger LOGGER = LoggerFactory.getLogger(ISeriesRepoImpl.class);

	@Autowired
	// Inject "self" so that we can use the methods
	private ISeriesRepo repo;

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public Series saveWithReverseRelations(Series s) {
		// Purge all backwards associations in related series
		Long currentId = s.getId();
		if (currentId != null) {
			LOGGER.trace("Purging all reverse associations to series {}", currentId);
			Query query = entityManager.createNativeQuery("DELETE FROM series_relations WHERE sub=:subId");
			query.setParameter("subId", currentId);
			query.executeUpdate();
		}

		Series saved = repo.save(s);
		long id = saved.getId();

		// Insert backwards associated in for all new related series
		if (s.getRelatedSeries() != null && !s.getRelatedSeries().isEmpty()) {
			LOGGER.trace("Inserting {} new reverse associations to series {}", s.getRelatedSeries().size(), id);

			Query query = entityManager
					.createNativeQuery("INSERT INTO series_relations(main, sub) VALUES (:mainId, :subId)");
			query.setParameter("subId", id);

			for (Series relation : s.getRelatedSeries()) {
				query.setParameter("mainId", relation.getId());
				query.executeUpdate();
			}
		}

		return saved;
	}
}
