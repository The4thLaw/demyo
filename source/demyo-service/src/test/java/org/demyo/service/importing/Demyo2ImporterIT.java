package org.demyo.service.importing;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import org.demyo.common.exception.DemyoException;
import org.demyo.dao.IAlbumRepo;
import org.demyo.dao.IImageRepo;
import org.demyo.dao.IPublisherRepo;
import org.demyo.dao.IRawSQLDao;
import org.demyo.model.Album;
import org.demyo.model.BookType;
import org.demyo.model.Reader;
import org.demyo.model.Taxon;
import org.demyo.model.beans.ReaderLists;
import org.demyo.model.enums.TaxonType;
import org.demyo.model.enums.TranslationLabelType;
import org.demyo.service.IAlbumService;
import org.demyo.service.IDerivativeService;
import org.demyo.service.IImageService;
import org.demyo.service.IPublisherService;
import org.demyo.service.IReaderService;
import org.demyo.service.ITaxonService;
import org.demyo.service.impl.AbstractServiceTest;
import org.demyo.service.impl.IBookTypeService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.demyo.test.assertions.model.Assertions.assertThat;

/**
 * Tests for the {@link Demyo2Importer}.
 */
class Demyo2ImporterIT extends AbstractServiceTest {
	@Autowired
	private IRawSQLDao rawSqlDao;

	@Autowired
	private IAlbumRepo albumRepo;
	@Autowired
	private IAlbumService albumService;

	@Autowired
	private IBookTypeService bookTypeService;

	@Autowired
	private IDerivativeService derivativeService;

	@Autowired
	private IImageRepo imageRepo;
	@Autowired
	private IImageService imageService;

	@Autowired
	private IPublisherRepo publisherRepo;
	@Autowired
	private IPublisherService publisherService;

	@Autowired
	private IReaderService readerService;
	@Autowired
	private ITaxonService taxonService;

	@Autowired
	@Qualifier("demyo2Importer")
	private IImporter importer;

	/**
	 * Tests the completeness of a Demyo 2+ import.
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

		Path sourceFile = Path.of("src/test/resources/demyo-2-export.xml");
		importer.importFile(sourceFile.getFileName().toString(), sourceFile);

		// Images
		assertThat(imageRepo.count()).isEqualTo(91);
		assertThat(imageService.getByIdForView(306))
			.hasUrl("dummy-image.jpg")
			.hasDescription("Lanfeust de Troy 5 - Couverture");
		assertThat(albumService.getByIdForView(310).getCover()).hasId(306L);

		assertThat(imageService.getByIdForView(309))
			.hasDescription("Lanfeust de Troy 7 - Couverture (version n&b)");
		assertThat(albumService.getByIdForView(312).getImages()).anyMatch(i -> i.getId().equals(309L));

		assertThat(imageService.getByIdForView(139))
			.hasDescription("Lanfeust de Troy 6 - Ex-Libris");
		assertThat(derivativeService.getByIdForView(53).getImages()).anyMatch(i -> i.getId().equals(139L));

		// Publishers
		assertThat(publisherRepo.count()).isEqualTo(3);
		/*assertThat(publisherService.getByIdForView(1))
				.hasName("Dargaud");*/
		/*documentAssert.css("publisher")
				.hasSize(3)
				.byId(1)
				.hasAttribute("name", "Dargaud")
				.hasAttribute("website", "http://www.dargaud.com/")
				.hasAttribute("feed", "http://www.dargaud.com/actualites/news.aspx")
				.hasAttribute("logo_id", 5014);
		documentAssert.xpathSingle("//album[@id=306]")
				.hasAttribute("publisher_id", 4);
		documentAssert.xpathSingle("//collection[@id=90]")
				.hasAttribute("publisher_id", 1);*/

		// TODO: migrate these

		assertThat(albumRepo.count()).isEqualTo(77);
		Album album306 = albumService.getByIdForView(306);
		assertThat(album306)
			.hasTitle("L'Ivoire du Magohamoth");
		assertThat(album306.getSeries()).hasName("Lanfeust de Troy");

		Album album1992 = albumService.getByIdForView(1992);
		assertThat(album1992.getPrintingDate()).isCloseTo("2021-02-01", 3_600);

		List<Taxon> taxons = taxonService.findAll();
		assertThat(taxons).hasSize(12);
		Taxon taxon21 = taxonService.getByIdForView(21);
		assertThat(taxon21)
			.hasName("one shot")
			.hasType(TaxonType.TAG);

		List<BookType> bookTypes = bookTypeService.findAll();
		assertThat(bookTypes).hasSize(1);
		BookType bookType1 = bookTypeService.getByIdForView(1);
		assertThat(bookType1).hasLabelType(TranslationLabelType.GRAPHIC_NOVEL);

		List<Reader> readers = readerService.findAll();
		assertThat(readers).hasSize(1);
		Reader reader = readers.get(0);

		ReaderLists lists = readerService.getLists(reader.getId());
		assertThat(lists.favouriteAlbums()).hasSize(1);
		assertThat(lists.favouriteAlbums()).contains(1362);

		assertThat(lists.favouriteSeries()).hasSize(3);
		assertThat(lists.favouriteSeries()).contains(169, 296, 312);

		assertThat(lists.readingList()).hasSize(15);
		assertThat(lists.readingList()).contains(1515);
	}
}
