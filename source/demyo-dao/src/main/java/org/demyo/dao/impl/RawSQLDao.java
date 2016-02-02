package org.demyo.dao.impl;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.sql.DataSource;

import org.demyo.dao.IRawSQLDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Implements the contract defined by {@link IRawSQLDao}.
 */
@Repository
public class RawSQLDao implements IRawSQLDao {

	@PersistenceContext
	private EntityManager entityManager;

	private JdbcTemplate jdbcTemplate;

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
		// To get the list of tables:
		// grep "CREATE TABLE" create-tables.sql | sed 's/CREATE TABLE /"/' | sed 's/ (/",/' | tac
		for (String table : new String[] { "searches", "albums_borrowers", "borrowers", "derivatives_images",
				"derivatives_prices", "derivatives", "derivative_types", "sources", "albums_tags", "tags",
				"albums_colorists", "albums_writers", "albums_artists", "albums_images", "albums_prices",
				"albums", "bindings", "series_relations", "series", "authors", "collections", "publishers",
				"images" }) {
			executeUpdate("DELETE FROM " + table);
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
		return ((BigInteger) query.getSingleResult()).longValue();
	}

	@Override
	public List<Map<String, Object>> getRawRecords(String tableName) {
		return jdbcTemplate.queryForList("SELECT * FROM " + tableName);
	}
}
