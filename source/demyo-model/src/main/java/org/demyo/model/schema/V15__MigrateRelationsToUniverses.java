package org.demyo.model.schema;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import org.demyo.model.schema.common.RelationsToUniverseConverter;

/**
 * Migrates the series_relations to sensible universes
 */
public class V15__MigrateRelationsToUniverses extends BaseJavaMigration {
	@Override
	public void migrate(Context context) throws Exception {
		Connection connection = context.getConnection();
		RelationsToUniverseConverter converter = new RelationsToUniverseConverter(connection);

		gatherGroups(connection, converter);

		converter.convert();
	}

	private static void gatherGroups(Connection connection, RelationsToUniverseConverter converter)
			throws SQLException {
		try (PreparedStatement stmt = connection.prepareStatement("select * from SERIES_RELATIONS")) {
			try (ResultSet rows = stmt.executeQuery()) {
				while (rows.next()) {
					converter.addToGroups(rows.getLong("main"), rows.getLong("sub"));
				}
			}
		}
	}

}
