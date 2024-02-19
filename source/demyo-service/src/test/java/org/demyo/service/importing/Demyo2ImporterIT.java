package org.demyo.service.importing;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import org.demyo.common.exception.DemyoException;
import org.demyo.dao.IRawSQLDao;
import org.demyo.model.Album;
import org.demyo.model.Reader;
import org.demyo.model.Tag;
import org.demyo.model.beans.ReaderLists;
import org.demyo.service.IAlbumService;
import org.demyo.service.IReaderService;
import org.demyo.service.ITagService;
import org.demyo.service.impl.AbstractServiceTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for the {@link Demyo2Importer}.
 */
class Demyo2ImporterIT extends AbstractServiceTest {
	@Autowired
	private IRawSQLDao rawSqlDao;
	@Autowired
	private IReaderService readerService;
	@Autowired
	private ITagService tagService;
	@Autowired
	private IAlbumService albumService;
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
	void testImport() throws IOException, DemyoException {
		// This is normally done by the import service, but injecting the import service is actually
		// a bit more tricky
		rawSqlDao.pruneAllTables();

		File sourceFile = new File("src/test/resources/demyo-2-export.xml");
		importer.importFile(sourceFile.getName(), sourceFile.toPath());

		List<Album> albums = albumService.findAll();
		assertThat(albums).hasSize(3);
		Album album1 = albumService.getByIdForView(1);
		assertThat(album1.getPrintingDate()).isEqualToIgnoringHours("1985-12-01");

		List<Tag> tags = tagService.findAll();
		assertThat(tags).hasSize(3);
		Tag tag2 = tags.get(1);
		assertThat(tag2.getDescription()).isEqualTo("rofl");

		List<Reader> readers = readerService.findAll();
		assertThat(readers).hasSize(1);
		Reader reader = readers.get(0);

		ReaderLists lists = readerService.getLists(reader.getId());
		assertThat(lists.favouriteAlbums()).hasSize(1);
		assertThat(lists.favouriteAlbums()).contains(1);

		assertThat(lists.favouriteSeries()).hasSize(1);
		assertThat(lists.favouriteSeries()).contains(2);

		assertThat(lists.readingList()).hasSize(1);
		assertThat(lists.readingList()).contains(3);
	}
}
