package org.demyo.model.schema;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This migration moves taxons applied to all albums of a series to the level of the series.
 * <p>
 * This migration is related to issue #14
 * </p>
 */
public class V20__MoveAlbumTaxonsToSeries extends BaseJavaMigration {
	private static final Logger LOGGER = LoggerFactory.getLogger(V20__MoveAlbumTaxonsToSeries.class);

	@Override
	public void migrate(Context context) throws Exception {
		Connection connection = context.getConnection();

		String searchQuery = """
				select
					s.ID as series_id, s.NAME as series_name, t.ID as taxon_id, t.NAME as taxon_name
				from
					SERIES s
					cross join TAXONS t
				where
					-- At least one album has the taxon
					exists
						(select * from ALBUMS a inner join ALBUMS_TAXONS at on at.ALBUM_ID = a.ID
							where SERIES_ID=s.ID and TAXON_ID = t.ID)
					-- There isn't an album that doesn't have the taxon
					and not exists
						(select * from ALBUMS a
						where SERIES_ID=s.ID
							and not exists (select * from ALBUMS_TAXONS where ALBUM_ID = a.ID and TAXON_ID=t.ID))
				order by s.NAME, t.NAME""";

		try (PreparedStatement search = connection.prepareStatement(searchQuery)) {
			try (ResultSet rows = search.executeQuery()) {
				LOGGER.debug("Starting migration of albums tags to the series");
				while (rows.next()) {
					long seriesId = rows.getLong("series_id");
					String seriesName = rows.getString("series_name");
					long taxonId = rows.getLong("taxon_id");
					String taxonName = rows.getString("taxon_name");

					LOGGER.debug("Moving taxon {} (#{}) from the albums to the series in {} (#{})",
							taxonName, taxonId, seriesName, seriesId);

					String insertQuery = "insert into SERIES_TAXONS(SERIES_ID, TAXON_ID) VALUES (?,?)";
					try (PreparedStatement insert = connection.prepareStatement(insertQuery)) {
						insert.setLong(1, seriesId);
						insert.setLong(2, taxonId);
						insert.executeUpdate();
					}

					String deleteQuery = "delete from ALBUMS_TAXONS where TAXON_ID = ? "
							+ "and ALBUM_ID in (select ID from ALBUMS where SERIES_ID = ?)";
					try (PreparedStatement insert = connection.prepareStatement(deleteQuery)) {
						insert.setLong(1, taxonId);
						insert.setLong(2, seriesId);
						insert.executeUpdate();
					}
				}
			}
		}
	}
}
