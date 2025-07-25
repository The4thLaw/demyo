package org.demyo.dao.impl;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import org.demyo.common.exception.DemyoErrorCode;
import org.demyo.common.exception.DemyoRuntimeException;
import org.demyo.dao.IRawSQLDao;

/**
 * Implements the contract defined by {@link IRawSQLDao}.
 */
@Repository
public class RawSQLDao implements IRawSQLDao {
	private static final Logger LOGGER = LoggerFactory.getLogger(RawSQLDao.class);

	@PersistenceContext
	private EntityManager entityManager;

	private final JdbcTemplate jdbcTemplate;

	/**
	 * Default construtor.
	 */
	public RawSQLDao() {
		jdbcTemplate = new JdbcTemplate();
	}

	@Autowired
	public void setDataSource(DataSource source) {
		jdbcTemplate.setDataSource(source);
	}

	private void executeUpdate(String sql) {
		Query query = entityManager.createNativeQuery(sql);
		query.executeUpdate();
	}

	@Override
	public void pruneAllTables() {
		LOGGER.debug("Pruning all tables");
		for (String table : new String[] { "searches", "albums_borrowers", "borrowers", "derivatives_images",
				"derivatives_prices", "derivatives", "derivative_types", "sources", "albums_tags", "tags",
				"readers_favourite_series", "readers_favourite_albums", "readers_reading_list", "albums_colorists",
				"albums_writers", "albums_artists", "albums_inkers", "albums_translators", "albums_cover_artists",
				"albums_images", "albums_prices", "albums", "bindings",
				"series", "authors", "collections", "publishers", "images", "readers",
				"configuration", "book_types", "universes" }) {
			executeUpdate("DELETE FROM " + table);
		}
	}

	@Override
	public void fixAutoIncrements() {
		LOGGER.debug("Fixing auto-increments");
		for (String table : new String[] { "albums", "authors", "bindings", "borrowers", "collections", "configuration",
				"derivative_types", "derivatives", "images", "publishers", "readers", "searches", "series", "sources",
				"tags", "book_types", "universes" }) {
			executeUpdate(
					"ALTER TABLE " + table + " ALTER COLUMN ID RESTART WITH (SELECT MAX(id) + 1 FROM " + table + ")");
		}
	}

	@Override
	public void insert(String tableName, Map<String, ? extends Object> values) {
		StringBuilder insertSb = new StringBuilder("INSERT INTO ").append(tableName).append('(');
		StringBuilder valuesSb = new StringBuilder(" VALUES (");

		for (String col : values.keySet()) {
			insertSb.append(col).append(',');
			valuesSb.append(':').append(col).append(',');
		}
		insertSb.deleteCharAt(insertSb.length() - 1); // Remove last comma
		valuesSb.deleteCharAt(valuesSb.length() - 1); // Remove last comma
		insertSb.append(')');
		valuesSb.append(')');
		insertSb.append(valuesSb);
		Query query = entityManager.createNativeQuery(insertSb.toString());
		for (Entry<String, ? extends Object> entry : values.entrySet()) {
			query.setParameter(entry.getKey(), entry.getValue());
		}
		query.executeUpdate();
	}

	@Override
	public long count(String tableName) {
		Query query = entityManager.createNativeQuery("SELECT COUNT(*) FROM " + tableName);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public List<Map<String, Object>> getRawRecords(String tableName) {
		return jdbcTemplate.queryForList("SELECT * FROM " + tableName);
	}

	@Override
	public int getSchemaVersion() {
		Integer version = jdbcTemplate.queryForObject(
				"select \"version\" from \"schema_version\" order by \"installed_rank\" desc LIMIT 1", Integer.class);
		if (version == null) {
			throw new DemyoRuntimeException(DemyoErrorCode.SYS_MISSING_DB_SCHEMA_VERSION);
		}
		return version;
	}

	@Override
	public void reassignBookTypes(long from, long to) {
		Query query = entityManager.createNativeQuery("UPDATE albums SET book_type_id = :to where book_type_id = :to");
		query.setParameter("from", from);
		query.setParameter("to", to);
		query.executeUpdate();
	}
}
