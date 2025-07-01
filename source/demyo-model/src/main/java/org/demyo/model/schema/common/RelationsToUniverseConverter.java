package org.demyo.model.schema.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RelationsToUniverseConverter {
	private static final Logger LOGGER = LoggerFactory.getLogger(RelationsToUniverseConverter.class);

	private final Connection connection;
	private final List<Set<Long>> groups = new ArrayList<>();

	public RelationsToUniverseConverter(Connection connection) {
		this.connection = connection;
	}

	public void addToGroups(long main, long sub) {
		// Find any existing group
		Optional<Set<Long>> groupOpt = groups.stream()
				.filter(s -> s.contains(main) || s.contains(sub))
				.findFirst();
		Set<Long> group = groupOpt.orElseGet(() -> {
			Set<Long> g = new TreeSet<>();
			groups.add(g);
			return g;
		});

		// Add the new IDs to the group. Duplicates will be normalized by the set
		group.add(main);
		group.add(sub);
	}

	public void convert() throws SQLException {
		LOGGER.info("Found {} series groups to migrate to universes", groups.size());

		for (Set<Long> group : groups) {
			// Find the name of the first series. Use the lowest ID as it's going to be the first
			// one the user purchased (except for Demyo 1.x users)
			long mainSeriesId = group.iterator().next();
			try (PreparedStatement seriesSelect = connection
					.prepareStatement("select NAME from SERIES where ID = ?1")) {
				seriesSelect.setLong(1, mainSeriesId);

				try (ResultSet sTitleRows = seriesSelect.executeQuery()) {
					sTitleRows.next();
					String title = sTitleRows.getString("NAME");
					createUniverse(group, title);
				}
			}
		}
	}

	private void createUniverse(Set<Long> series, String title) throws SQLException {
		try (PreparedStatement univInsert = connection.prepareStatement(
				"insert into UNIVERSES(NAME) values (?1)", Statement.RETURN_GENERATED_KEYS)) {
			univInsert.setString(1, title);
			univInsert.executeUpdate();
			try (ResultSet keys = univInsert.getGeneratedKeys()) {
				keys.next();
				long univId = keys.getLong(1);
				setUniverseOnSeries(series, univId);
			}
		}
	}

	private void setUniverseOnSeries(Set<Long> series, long univId) throws SQLException {
		String seriesUpdateSql = String.format("update SERIES set UNIVERSE_ID = ? where ID in (%s)",
				series.stream()
						.map(v -> "?")
						.collect(Collectors.joining(", ")));
		try (PreparedStatement seriesUpdate = connection.prepareStatement(seriesUpdateSql)) {
			seriesUpdate.setLong(1, univId);
			int i = 2;
			for (long seriesId : series) {
				seriesUpdate.setLong(i++, seriesId);
			}
			seriesUpdate.executeUpdate();
		}
	}
}
