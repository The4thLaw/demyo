package org.demyo.service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.sql.DataSource;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import org.demyo.common.exception.DemyoException;
import org.demyo.service.impl.AbstractServiceTest;

/**
 * Utility class to import a demyo XML file and export it to the dbunit test XML.
 * <p>
 * Not actually meant to be run as a JUnit test in CI.
 * </p>
 */
@Disabled("This is just an utility class") // Comment this to launch this utility
class DbUnitExtractorIT extends AbstractServiceTest {
	@Autowired
	private IImportService importService;
	@Autowired
	private DataSource jdbcConnection;
	@Autowired
	private PlatformTransactionManager txMgr;

	@Test
	void importXmlAndExportDBUnit() throws Exception {
		// Manage transaction manually to be able to read directly afterwards
		TransactionTemplate transactionTemplate = new TransactionTemplate(txMgr);
		transactionTemplate.execute(new TransactionCallback<Void>() {

			@Override
			public Void doInTransaction(TransactionStatus status) {
				try (InputStream is = new ClassPathResource("/org/demyo/test/demyo-sample-import.xml")
						.getInputStream()) {
					importService.importFile("demyo.xml", is);
				} catch (IOException | DemyoException e) {
					throw new RuntimeException(e);
				}
				return null;
			}
		});

		// Gather data set
		IDatabaseConnection connection = new DatabaseConnection(jdbcConnection.getConnection());
		QueryDataSet fullDataSet = new QueryDataSet(connection);
		// The order of this list is important: DBUnit will keep the same and may fail if foreign keys are missing
		for (String table : new String[] { "IMAGES", "PUBLISHERS", "COLLECTIONS", "AUTHORS", "SERIES",
				"SERIES_RELATIONS", "TAGS", "BINDINGS", "ALBUMS", "ALBUMS_PRICES", "ALBUMS_IMAGES", "ALBUMS_ARTISTS",
				"ALBUMS_WRITERS", "ALBUMS_COLORISTS", "ALBUMS_INKERS", "ALBUMS_TRANSLATORS", "ALBUMS_TAGS", "SOURCES",
				"DERIVATIVE_TYPES", "DERIVATIVES", "DERIVATIVES_PRICES", "DERIVATIVES_IMAGES", "READERS",
				"READERS_FAVOURITE_SERIES", "READERS_FAVOURITE_ALBUMS", "READERS_READING_LIST", "CONFIGURATION" }) {
			fullDataSet.addTable(table);
		}

		// Write to src/test
		Path output = Path.of("../demyo-test/src/main/resources/org/demyo/test/demyo-dbunit-standard.xml");
		// Note that we can't have emojis in the test database. DBUnit uses the same code internally that caused issue
		// https://github.com/The4thLaw/demyo/issues/76
		try (BufferedWriter writer = Files.newBufferedWriter(output, StandardCharsets.UTF_8)) {
			FlatXmlDataSet.write(fullDataSet, writer, StandardCharsets.UTF_8.name());
		}
	}
}
