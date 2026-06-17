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
import org.demyo.dao.IAuthorRepo;
import org.demyo.dao.IBindingRepo;
import org.demyo.dao.ICollectionRepo;
import org.demyo.dao.IImageRepo;
import org.demyo.dao.IPublisherRepo;
import org.demyo.dao.IRawSQLDao;
import org.demyo.model.Author;
import org.demyo.model.Publisher;
import org.demyo.model.Reader;
import org.demyo.model.beans.ReaderLists;
import org.demyo.service.IAlbumService;
import org.demyo.service.IAuthorService;
import org.demyo.service.IBindingService;
import org.demyo.service.ICollectionService;
import org.demyo.service.IDerivativeService;
import org.demyo.service.IImageService;
import org.demyo.service.IPublisherService;
import org.demyo.service.IReaderService;
import org.demyo.service.ITaxonService;
import org.demyo.service.impl.AbstractServiceTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.demyo.test.assertions.model.Assertions.assertThat;

/**
 * Tests for the {@link Demyo2Importer}.
 */
class Demyo2ImporterIT extends AbstractServiceTest {
	private static final String SAMPLE_HTML_DESCRIPTION = "<p>Sample HTML description</p>";

	@Autowired
	private IRawSQLDao rawSqlDao;

	@Autowired
	private IImageRepo imageRepo;
	@Autowired
	private IImageService imageService;

	@Autowired
	private IPublisherRepo publisherRepo;
	@Autowired
	private IPublisherService publisherService;

	@Autowired
	private ICollectionRepo collectionRepo;
	@Autowired
	private ICollectionService collectionService;

	@Autowired
	private IBindingRepo bindingRepo;
	@Autowired
	private IBindingService bindingService;

	@Autowired
	private IAuthorRepo authorRepo;
	@Autowired
	private IAuthorService authorService;

	@Autowired
	private IAlbumRepo albumRepo;
	@Autowired
	private IAlbumService albumService;

	@Autowired
	private IDerivativeService derivativeService;

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
		assertThat(imageRepo.count()).isEqualTo(161);
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
		assertThat(publisherRepo.count()).isEqualTo(7);
		Publisher dargaud = publisherService.getByIdForView(1);
		assertThat(dargaud)
				.hasName("Dargaud")
				.hasWebsite("http://www.dargaud.com/")
				.hasFeed("http://www.dargaud.com/actualites/news.aspx");
		assertThat(dargaud.getLogo()).hasId(5014L);
		assertThat(albumService.getByIdForView(306).getPublisher()).hasId(4L);
		assertThat(publisherService.getByIdForView(4)).hasHistory(SAMPLE_HTML_DESCRIPTION);

		// Collections
		// Note: can't test the feed, no-one offers it anymore at this level
		assertThat(collectionRepo.count()).isEqualTo(36);
		assertThat(collectionService.getByIdForView(90))
				.hasName("Fictions")
				.hasWebsite("http://www.dargaud.com/front/albums/collections/collection.aspx?id=954")
				.hasPublisher(dargaud);
		assertThat(collectionService.getByIdForView(38))
				.hasHistory(SAMPLE_HTML_DESCRIPTION);
		assertThat(collectionService.getByIdForView(31).getLogo()).hasId(7083L);

		// Bindings
		assertThat(bindingRepo.count()).isEqualTo(3);
		assertThat(bindingService.getByIdForView(1))
				.hasName("Cartonné");

		// Authors
		assertThat(authorRepo.count()).isEqualTo(41);
		assertThat(authorService.getByIdForView(119))
				.hasName("Tarquin")
				.hasFirstName("Didier")
				.hasBiography(SAMPLE_HTML_DESCRIPTION);
		assertThat(authorService.getByIdForView(216))
				.hasNickname("Fred");
		Author beAuthor = authorService.getByIdForView(282);
		assertThat(beAuthor).hasCountry("BEL");
		assertThat(beAuthor.getBirthDate()).isInSameDayAs("1937-03-14");
		assertThat(beAuthor.getDeathDate()).isInSameDayAs("1976-01-21");
		assertThat(authorService.getByIdForView(311))
				.hasWebsite("https://seblamirand.blogspot.com/");
		assertThat(authorService.getByIdForView(120).getPortrait()).hasId(6737L);
		assertThat(authorService.getByIdForView(658).getPseudonymOf()).hasId(120L);

		// TODO: series
		// TODO: taxons
		// TODO: book types
		// TODO: universes
		// TODO: albums
		// TODO: .contains("<album_price album_id=\"313\" date=\"2010-09-26\" price=\"15.0\" />")
		// TODO: derivative types
		// TODO: derivatives
		// TODO: .contains("<derivative_price derivative_id=\"53\" date=\"2010-09-26\" price=\"30.0\" />")

		List<Reader> readers = readerService.findAll();
		assertThat(readers).hasSize(1);
		Reader reader = readers.get(0);

		ReaderLists lists = readerService.getLists(reader.getId());
		assertThat(lists.favouriteAlbums()).hasSize(1);
		assertThat(lists.favouriteAlbums()).contains(1362);

		assertThat(lists.favouriteSeries()).hasSize(3);
		assertThat(lists.favouriteSeries()).contains(169, 296, 312);

		assertThat(lists.readingList()).hasSize(28);
		assertThat(lists.readingList()).contains(1515);
	}
}
