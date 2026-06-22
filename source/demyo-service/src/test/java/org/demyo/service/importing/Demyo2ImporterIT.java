package org.demyo.service.importing;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import org.demyo.common.exception.DemyoException;
import org.demyo.dao.IRawSQLDao;
import org.demyo.model.AbstractPricedModel;
import org.demyo.model.Album;
import org.demyo.model.AlbumPrice;
import org.demyo.model.Author;
import org.demyo.model.IModel;
import org.demyo.model.Publisher;
import org.demyo.model.Reader;
import org.demyo.model.Series;
import org.demyo.model.Universe;
import org.demyo.model.beans.ReaderLists;
import org.demyo.model.enums.ModelField;
import org.demyo.model.enums.TaxonType;
import org.demyo.model.enums.TranslationLabelType;
import org.demyo.service.IAlbumService;
import org.demyo.service.IAuthorService;
import org.demyo.service.IBindingService;
import org.demyo.service.ICollectionService;
import org.demyo.service.IDerivativeService;
import org.demyo.service.IDerivativeTypeService;
import org.demyo.service.IImageService;
import org.demyo.service.IPublisherService;
import org.demyo.service.IReaderService;
import org.demyo.service.ISeriesService;
import org.demyo.service.ITaxonService;
import org.demyo.service.IUniverseService;
import org.demyo.service.impl.AbstractServiceTest;
import org.demyo.service.impl.IBookTypeService;

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
	private IImageService imageService;

	@Autowired
	private IPublisherService publisherService;

	@Autowired
	private ICollectionService collectionService;

	@Autowired
	private IBindingService bindingService;

	@Autowired
	private IAuthorService authorService;

	@Autowired
	private ISeriesService seriesService;

	@Autowired
	private ITaxonService taxonService;

	@Autowired
	private IBookTypeService bookTypeService;

	@Autowired
	private IUniverseService universeService;

	@Autowired
	private IAlbumService albumService;

	@Autowired
	private IDerivativeTypeService derivativeTypeService;

	@Autowired
	private IDerivativeService derivativeService;

	@Autowired
	private IReaderService readerService;

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
		assertThat(imageService.count()).isEqualTo(161);
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
		assertThat(publisherService.count()).isEqualTo(7);
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
		assertThat(collectionService.count()).isEqualTo(36);
		assertThat(collectionService.getByIdForView(90))
				.hasName("Fictions")
				.hasWebsite("http://www.dargaud.com/front/albums/collections/collection.aspx?id=954")
				.hasPublisher(dargaud);
		assertThat(collectionService.getByIdForView(38))
				.hasHistory(SAMPLE_HTML_DESCRIPTION);
		assertThat(collectionService.getByIdForView(31).getLogo()).hasId(7083L);

		// Bindings
		assertThat(bindingService.count()).isEqualTo(3);
		assertThat(bindingService.getByIdForView(1))
				.hasName("Cartonné");

		// Authors
		assertThat(authorService.count()).isEqualTo(41);
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

		// Series
		assertThat(seriesService.count()).isEqualTo(14);
		Series blame = seriesService.getByIdForView(132);
		assertThat(blame)
				.hasName("Blame!")
				.hasOriginalName("BLAME!")
				.hasLocation("Salon, G5")
				.hasCompleted(true)
				.hasWebsite("http://en.wikipedia.org/wiki/Blame!");
		assertThat(blame.getUniverse()).hasId(18L);
		assertThat(seriesService.getByIdForView(142L)).hasSummary(SAMPLE_HTML_DESCRIPTION);
		assertThat(seriesService.getByIdForView(320L)).hasComment(SAMPLE_HTML_DESCRIPTION);

		// Taxons
		assertThat(taxonService.count()).isEqualTo(23);
		assertThat(taxonService.getByIdForView(21))
				.hasName("one shot")
				.hasType(TaxonType.TAG);
		assertThat(albumService.getByIdForView(653)
				.getTaxons()
				.stream()
				.map(IModel::getId)
				.toList()).containsExactlyInAnyOrder(5L, 10L, 29L /* from series */, 128L);
		assertThat(seriesService.getByIdForView(69).getTaxons()).hasSize(1)
				.allMatch(t -> t.getId().equals(5L));

		// Book types
		assertThat(bookTypeService.count()).isEqualTo(4);
		assertThat(bookTypeService.getByIdForView(3))
				.hasName("Roman")
				.hasLabelType(TranslationLabelType.NOVEL)
				.hasDescription(SAMPLE_HTML_DESCRIPTION)
				.hasStructuredFieldConfig(ModelField.ALBUM_COLORIST, ModelField.ALBUM_INKER);
		assertThat(albumService.getByIdForView(306).getBookType()).hasId(1L);

		// Universes
		assertThat(universeService.count()).isEqualTo(5);
		Universe mondesDeTroy = universeService.getByIdForView(8);
		assertThat(mondesDeTroy).hasName("Mondes de Troy");
		assertThat(mondesDeTroy.getLogo()).hasId(7085L);
		assertThat(mondesDeTroy.getImages())
				.hasSize(1)
				.allMatch(i -> i.getId().equals(1091L));
		assertThat(universeService.getByIdForView(30)).hasDescription(SAMPLE_HTML_DESCRIPTION);

		// Albums
		assertThat(albumService.count()).isEqualTo(109);
		Album magohamoth = albumService.getByIdForView(306);
		assertThat(magohamoth)
				.hasCycle(1)
				.hasNumber(new BigDecimal("1.0"))
				.hasNumberSuffix("ré")
				.hasTitle("L'Ivoire du Magohamoth")
				.isNotWishlist()
				.hasComment(SAMPLE_HTML_DESCRIPTION)
				.hasHeight(new BigDecimal("320.0"))
				.hasWidth(new BigDecimal("230.0"))
				.hasPages(44)
				.isNotMarkedAsFirstEdition();
		assertThat(magohamoth.getSeries()).hasId(69L);
		assertThat(magohamoth.getFirstEditionDate()).isInSameDayAs("1994-10-01");
		assertThat(magohamoth.getCurrentEditionDate()).isInSameDayAs("1996-10-01");
		assertThat(magohamoth.getBinding()).hasId(1L);
		assertThat(magohamoth.getCover()).hasId(302L);
		assertThat(albumService.getByIdForView(1992).getPrintingDate()).isInSameDayAs("2021-02-01");
		Album orAzur = albumService.getByIdForView(796);
		assertThat(orAzur)
				.hasIsbn("978-2-30200-869-4")
				.hasSummary(SAMPLE_HTML_DESCRIPTION);
		assertThat(albumService.getByIdForView(2892)).hasLocation("Mezzanine, D3");
		assertThat(albumService.getByIdForView(3962)).hasOriginalTitle("Πλάτωνος Ἀπολογία Σωκράτους");
		assertThat(albumService.getByIdForView(1024).getCollection()).hasId(33L);
		Album foretNoiseuse = albumService.getByIdForView(2030);
		assertThat((AbstractPricedModel<?, ?>) foretNoiseuse).hasPurchasePrice(new BigDecimal("15.0"));
		assertThat(foretNoiseuse.getAcquisitionDate()).isInSameDayAs("2022-05-02");
		assertThat(albumService.getByIdForView(1128).getUniverse()).hasId(18L);
		Album alunys = albumService.getByIdForView(1520);
		assertThat(alunys.getArtists())
				.hasSize(1)
				.allMatch(a -> a.getId().equals(875L));
		assertThat(alunys.getWriters().stream().map(Author::getId).toList())
				.hasSize(2)
				.containsExactlyInAnyOrder(120L, 873L);
		assertThat(alunys.getColorists())
				.hasSize(1)
				.allMatch(a -> a.getId().equals(730L));
		assertThat(alunys.getInkers())
				.hasSize(1)
				.allMatch(a -> a.getId().equals(876L));
		Album twoTowers = albumService.getByIdForView(3997L);
		assertThat(twoTowers.getTranslators())
				.hasSize(1)
				.allMatch(a -> a.getId().equals(2115L));
		assertThat(twoTowers.getCoverArtists())
				.hasSize(1)
				.allMatch(a -> a.getId().equals(2116L));
		Album beteFabuleuse = albumService.getByIdForView(313);
		assertThat(beteFabuleuse.getPriceList())
				.hasSize(2);
		AlbumPrice bfp1 = beteFabuleuse.getPriceList().get(0);
		assertThat(bfp1.getPrice()).isEqualTo("15.0");
		assertThat(bfp1.getDate()).isInSameDayAs("2010-09-26");

		// Derivative types
		assertThat(derivativeTypeService.count()).isEqualTo(5);
		assertThat(derivativeTypeService.getByIdForView(1)).hasName("Offset");

		// TODO: derivatives
		/*
		documentAssert.css("derivative")
				.hasSize(43)
				.byId(433)
				.hasAttribute("series_id", 320)
				.hasAttribute("album_id", 1516)
				.hasAttribute("artist_id", 233)
				.hasAttribute("derivative_type_id", 2)
				.hasAttribute("colors", 3)
				.hasAttribute("source_id", 3)
				.hasAttribute("signed", true)
				.hasAttribute("authors_copy", true)
				.hasAttribute("restricted_sale", false)
				.hasAttribute("description", SAMPLE_HTML_DESCRIPTION)
				.hasAttribute("width", "160.0")
				.hasAttribute("height", "240.0")
				.hasAttribute("acquisition_date", "2019-02-02")
				.hasAttribute("purchase_price", "15.0");
		documentAssert.xpathSingle("//derivative[@id=113]")
				.hasAttribute("depth", "5.0");
		documentAssert.xpathSingle("//derivative[@id=54]")
				.hasAttribute("number", 267)
				.hasAttribute("total", 325);
		documentAssert.xpath("//derivative[@id=53]/derivative-images/derivative-image")
				.hasSize(1)
				.at(0)
				.hasAttribute("ref", 139);
		 */
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
