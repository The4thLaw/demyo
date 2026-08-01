package org.demyo.dao.impl;

import javax.sql.DataSource;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.stereotype.Repository;
import org.the4thlaw.common.dao.impl.BaseDatabaseDao;

import org.demyo.common.exception.DemyoErrorCode;
import org.demyo.common.exception.DemyoRuntimeException;
import org.demyo.dao.IRawSQLDao;

/**
 * Implements the contract defined by {@link IRawSQLDao}.
 */
@Repository
public class RawSQLDao extends BaseDatabaseDao implements IRawSQLDao {
	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * Default construtor.
	 */
	public RawSQLDao(EntityManager entityManager, DataSource dataSource) {
		super("schema_version",
				new String[]
				{ "albums", "authors", "bindings", "borrowers", "collections", "configuration",
						"derivative_types", "derivatives", "images", "publishers", "readers", "searches", "series",
						"sources", "taxons", "book_types", "universes" },
				new String[]
				{ "searches", "albums_borrowers", "derivatives_images",
						"derivatives_prices", "albums_taxons", "series_taxons",
						"readers_favourite_series", "readers_favourite_albums", "readers_reading_list",
						"albums_colorists", "albums_writers", "albums_artists", "albums_inkers", "albums_translators",
						"albums_cover_artists", "albums_images", "albums_prices" },
				entityManager, dataSource);
	}

	@Override
	public void reassignBookTypes(long from, long to) {
		Query query = entityManager.createNativeQuery("UPDATE albums SET book_type_id = :to where book_type_id = :to");
		query.setParameter("from", from);
		query.setParameter("to", to);
		query.executeUpdate();
	}

	@Override
	public void setAuthorPseudonym(String authorId, String pseudonymOfId) {
		Query query = entityManager.createNativeQuery(
				"update AUTHORS set PSEUDONYM_OF_ID = :pseudonymOfId where ID = :authorId");
		query.setParameter("authorId", authorId);
		query.setParameter("pseudonymOfId", pseudonymOfId);
		query.executeUpdate();
	}

	@Override
	public int getSchemaVersion() {
		try {
			return super.getSchemaVersion();
		} catch (RuntimeException e) {
			throw new DemyoRuntimeException(DemyoErrorCode.SYS_MISSING_DB_SCHEMA_VERSION);
		}
	}
}
