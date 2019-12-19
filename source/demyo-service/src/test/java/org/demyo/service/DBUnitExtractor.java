package org.demyo.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sql.DataSource;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
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
 * Not actually meant to be run as a JUnit test.
 * </p>
 */
public class DBUnitExtractor extends AbstractServiceTest {
	@Autowired
	private IImportService importService;
	@Autowired
	private DataSource jdbcConnection;
	@Autowired
	private PlatformTransactionManager txMgr;

	// @Test // Uncomment this to launch this utility
	public void importXmlAndExportDBUnit() throws Exception {
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
		for (String table : new String[] { "IMAGES", "PUBLISHERS", "COLLECTIONS", "AUTHORS", "SERIES",
				"SERIES_RELATIONS", "ALBUMS", "ALBUMS_PRICES", "ALBUMS_IMAGES", "ALBUMS_ARTISTS", "ALBUMS_WRITERS",
				"ALBUMS_COLORISTS", "ALBUMS_INKERS", "ALBUMS_TRANSLATORS", "TAGS", "ALBUMS_TAGS", "SOURCES",
				"DERIVATIVE_TYPES", "DERIVATIVES", "DERIVATIVES_PRICES", "DERIVATIVES_IMAGES", "READERS",
				"READERS_FAVOURITE_SERIES", "READERS_FAVOURITE_ALBUMS", "READERS_READING_LIST", "CONFIGURATION" }) {
			fullDataSet.addTable(table);
		}

		// Write to src/test
		File output = new ClassPathResource("/org/demyo/test/demyo-dbunit-standard.xml").getFile();
		System.err.println("Writing to " + output);
		try (FileOutputStream os = new FileOutputStream(output)) {
			FlatXmlDataSet.write(fullDataSet, os);
		}
	}
}
