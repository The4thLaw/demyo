package org.demyo.dao.impl;

import javax.persistence.Query;

import org.demyo.dao.ISeriesDao;
import org.demyo.model.Series;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

/**
 * Implements the contract defined by {@link ISeriesDao}.
 * 
 * @author $Author: xr $
 * @version $Revision: 1071 $
 */
@Repository
public class SeriesDao extends AbstractModelDao<Series> implements ISeriesDao {
	private static final Logger LOGGER = LoggerFactory.getLogger(SeriesDao.class);

	/**
	 * Default constructor.
	 */
	public SeriesDao() {
		super(Series.class);
	}

	@Override
	public long save(Series model) {

		// Purge all backwards associations in related series
		Long currentId = model.getId();
		if (currentId != null) {
			LOGGER.trace("Purging all reverse associations to series {}", currentId);
			Query query = getEntityManager().createNativeQuery("DELETE FROM series_relations WHERE sub=:subId");
			query.setParameter("subId", currentId);
			query.executeUpdate();
		}

		// Save
		long id = super.save(model);

		// Insert backwards associated in for all new related series
		if (model.getRelatedSeries() != null && !model.getRelatedSeries().isEmpty()) {
			LOGGER.trace("Inserting {} new reverse associations to series {}", model.getRelatedSeries().size(), id);

			Query query = getEntityManager().createNativeQuery(
					"INSERT INTO series_relations(main, sub) VALUES (:mainId, :subId)");
			query.setParameter("subId", id);

			for (Series relation : model.getRelatedSeries()) {
				query.setParameter("mainId", relation.getId());
				query.executeUpdate();
			}
		}

		return id;
	}
}
