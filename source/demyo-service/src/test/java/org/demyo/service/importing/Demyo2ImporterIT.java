package org.demyo.service.importing;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import org.demyo.common.exception.DemyoException;
import org.demyo.dao.IRawSQLDao;
import org.demyo.model.Reader;
import org.demyo.service.IReaderService;
import org.demyo.service.impl.AbstractServiceTest;

/**
 * Tests for the {@link Demyo2Importer}.
 */
// @DatabaseSetup(value = "/org/demyo/test/demyo-dbunit-standard.xml", type = DatabaseOperation.REFRESH)
public class Demyo2ImporterIT extends AbstractServiceTest {
	@Autowired
	private IRawSQLDao rawSqlDao;
	@Autowired
	private IReaderService readerService;
	@Autowired
	@Qualifier("demyo2Importer")
	private IImporter importer;

	/**
	 * Tests the completeness of a Demyo 2 import.
	 * 
	 * @throws IOException In case of I/O error.
	 * @throws DemyoException In case of import error.
	 */
	@Test
	@Transactional(readOnly = true)
	public void testImport() throws IOException, DemyoException {
		// This is normally done by the import service, but injecting the import service is actually
		// a bit more tricky
		rawSqlDao.pruneAllTables();

		File sourceFile = new File("src/test/resources/demyo-2-export.xml");
		importer.importFile(sourceFile.getName(), sourceFile);

		List<Reader> readers = readerService.findAll();
		assertThat(readers).hasSize(1);
		Reader reader = readers.get(0);

		assertThat(reader.getFavouriteAlbums()).hasSize(1);
		assertThat(reader.getFavouriteAlbums().first().getId()).isEqualTo(1);

		assertThat(reader.getFavouriteSeries()).hasSize(1);
		assertThat(reader.getFavouriteSeries().first().getId()).isEqualTo(2);

		assertThat(reader.getReadingList()).hasSize(1);
		assertThat(reader.getReadingList().first().getId()).isEqualTo(3);
	}
}
