package org.demyo.dao;

import java.util.HashSet;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Implements the contract defined by {@link IImageCustomRepo}.
 */
public class IImageRepoImpl implements IImageCustomRepo {

	private JdbcTemplate jdbcTemplate;

	public IImageRepoImpl() {
		jdbcTemplate = new JdbcTemplate();
	}

	@Autowired
	public void setDataSource(DataSource source) {
		jdbcTemplate.setDataSource(source);
	}

	@Override
	public Set<String> findAllPaths() {
		return new HashSet<String>(jdbcTemplate.queryForList("select url from images", String.class));
	}

}
