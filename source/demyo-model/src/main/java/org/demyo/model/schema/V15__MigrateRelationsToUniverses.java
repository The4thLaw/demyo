package org.demyo.model.schema;

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

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Migrates the series_relations to sensible universes
 */
public class V15__MigrateRelationsToUniverses extends BaseJavaMigration {
	private static final Logger LOGGER = LoggerFactory.getLogger(V15__MigrateRelationsToUniverses.class);

	@Override
	public void migrate(Context context) throws Exception {
		Connection connection = context.getConnection();

		List<Set<Long>> groups = gatherGroups(connection);

		LOGGER.info("Found {} series groups to migrate to universes", groups.size());

		for (Set<Long> group : groups) {
			// Find the name of the first series. Use the lowest ID as it's going to be the first
			// one the user purchased (except for Demyo 1.x users)
			long mainSeriesId = group.iterator().next();
			try (PreparedStatement seriesSelect = context.getConnection()
					.prepareStatement("select NAME from SERIES where ID = ?1")) {
				seriesSelect.setLong(1, mainSeriesId);
				try (ResultSet rows = seriesSelect.executeQuery()) {
					rows.next();
					String title = rows.getString("NAME");
					try (PreparedStatement univInsert = context.getConnection().prepareStatement(
							"insert into UNIVERSES(NAME) values (?1)", Statement.RETURN_GENERATED_KEYS)) {
						univInsert.setString(1, title);
						univInsert.executeUpdate();
						try (ResultSet keys = univInsert.getGeneratedKeys()) {
							keys.next();
							long univId = keys.getLong(1);
							var seriesUpdateSql = String.format("update SERIES set UNIVERSE_ID = ? where ID in (%s)",
									group.stream()
											.map(v -> "?")
											.collect(Collectors.joining(", ")));
							try (PreparedStatement seriesUpdate = context.getConnection()
									.prepareStatement(seriesUpdateSql)) {
								seriesUpdate.setLong(1, univId);
								int i = 2;
								for (long seriesId : group) {
									seriesUpdate.setLong(i++, seriesId);
								}
								seriesUpdate.executeUpdate();
							}
							// TODO: extract all insert behaviour to something that can be shared with the import based
							// on the groups and connection ?
							// Check DataSourceUtils.getConnection(...)
						}

					}
				}
			}
		}
	}

	private List<Set<Long>> gatherGroups(Connection connection) throws SQLException {
		List<Set<Long>> groups = new ArrayList<>();
		try (PreparedStatement stmt = connection.prepareStatement("select * from SERIES_RELATIONS")) {
			try (ResultSet rows = stmt.executeQuery()) {
				while (rows.next()) {
					long main = rows.getLong("main");
					long sub = rows.getLong("sub");

					// Find any existing group
					Optional<Set<Long>> groupOpt = groups.stream().filter(s -> s.contains(main) || s.contains(sub))
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
			}
		}
		return groups;
	}

}
