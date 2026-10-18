package org.demyo.service.importing;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.the4thlaw.commons.services.importing.BaseXmlHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.demyo.dao.IRawSQLDao;
import org.demyo.model.schema.common.RelationsToUniverseConverter;

/**
 * SAX handler for import of Demyo 2.x files.
 */
public class Demyo2Handler extends BaseXmlHandler<IRawSQLDao, RelationsHolder> {
	private static final Logger LOGGER = LoggerFactory.getLogger(Demyo2Handler.class);

	private boolean hasBookType;
	private String seriesId;
	private String albumId;
	private String derivativeId;
	private String universeId;
	private String readerId;

	private final RelationsToUniverseConverter rtuConverter;

	/**
	 * Initializes structures for the handler.
	 */
	public Demyo2Handler(IRawSQLDao rawSqlDao, DataSource dataSource) {
		super(rawSqlDao, new RelationsHolder());
		rtuConverter = new RelationsToUniverseConverter(DataSourceUtils.getConnection(dataSource));
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes)
			throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		switch (localName) {
			case "book_type":
				hasBookType = true;
				// Fall-through
			case "image", "publisher", "collection", "binding", "taxon", "borrower", "source", //
					"derivative_type":
				createLine(localName + "s", attributes);
				break;
			case "author":
				Map<String, String> authorAttributes = toMap(attributes);
				String pseudonymOf = authorAttributes.get("pseudonym_of_id");
				if (StringUtils.isNotBlank(pseudonymOf)) {
					// Some authors may reference a later author as pseudonym and that breaks referential integrity
					// This can happen if you first encode the pseudonym as a specific author and later add
					// the real identity
					String id = authorAttributes.get("id");
					authorAttributes.remove("pseudonym_of_id");
					relations.addAuthorPseudonym(id, pseudonymOf);
				}
				createLine("authors", authorAttributes);
				break;
			// Before 3.1, we used tags
			case "tag":
				createLine("taxons", attributes);
				break;
			case "series":
				seriesId = attributes.getValue("id");
				createLine("series", attributes);
				break;
			case "related_series":
				rtuConverter.addToGroups(
						Long.valueOf(seriesId),
						Long.valueOf(attributes.getValue("ref")));
				break;
			case "albums":
				if (!hasBookType) {
					// If by now we don't have a book type, we should create the default one
					// This behaviour is a bit peculiar but it's the first time we have a non-backwards compatible
					// change
					// so let's deal with it the best we can
					LOGGER.info(
							"Migrating an old import: the default book type will be created and assigned to all books");
					Map<String, Object> bookType = new HashMap<>();
					bookType.put("id", 1L);
					bookType.put("name", "__DEFAULT__");
					bookType.put("label_type", "GRAPHIC_NOVEL");
					createLine("book_types", bookType);
				}
				break;
			case "album":
				albumId = attributes.getValue("id");
				Map<String, String> albumAttributes = toMap(attributes);
				if (!hasBookType) {
					// Set the book type we just created
					albumAttributes.put("book_type_id", "1");
				}
				createLine("albums", albumAttributes);
				break;
			case "artist":
				relations.addAlbumArtist(albumId, attributes);
				break;
			case "writer":
				relations.addAlbumWriters(albumId, attributes);
				break;
			case "colorist":
				relations.addAlbumColorists(albumId, attributes);
				break;
			case "inker":
				relations.addAlbumInkers(albumId, attributes);
				break;
			case "translator":
				relations.addAlbumTranslators(albumId, attributes);
				break;
			case "cover-artist":
				relations.addAlbumCoverArtists(albumId, attributes);
				break;
			case "series-taxon":
				relations.addSeriesTaxon(seriesId, attributes);
				break;
			// Before v3.1, we used tags
			case "album-tag", "album-taxon":
				relations.addAlbumTaxon(albumId, attributes);
				break;
			case "album-image":
				relations.addAlbumImage(albumId, attributes);
				break;
			case "album_price":
				createLine("albums_prices", attributes);
				break;
			case "loan":
				createLine("albums_borrowers", attributes);
				break;
			case "derivative":
				derivativeId = attributes.getValue("id");
				createLine("derivatives", attributes);
				break;
			case "derivative-image":
				relations.addDerivativeImage(derivativeId, attributes);
				break;
			case "derivative_price":
				createLine("derivatives_prices", attributes);
				break;
			case "universe":
				universeId = attributes.getValue("id");
				createLine("universes", attributes);
				break;
			case "universe-image":
				relations.addUniverseImage(universeId, attributes);
				break;
			case "reader":
				readerId = attributes.getValue("id");
				createLine("readers", attributes);
				break;
			case "favourite-series":
				relations.addReaderFavouriteSeries(readerId, attributes);
				break;
			case "favourite-album":
				relations.addReaderFavouriteAlbum(readerId, attributes);
				break;
			case "reading-list-entry":
				relations.addReaderReadingList(readerId, attributes);
				break;
			case "configuration-entry":
				createLine("configuration", attributes);
				break;
			default:
				// Do nothing
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		super.endElement(uri, localName, qName);
		switch (localName) {
			case "series":
				seriesId = null;
				break;
			case "albums":
				albumId = null;
				break;
			case "derivatives":
				derivativeId = null;
				break;
			case "readers":
				readerId = null;
				break;
			default:
				// No special behaviour for other tags
		}
	}

	@Override
	protected void persistRelations() throws SAXException {
		super.persistRelations();
		LOGGER.debug("Persisting deferred relationships");
		for (Pair<String, String> authorPseudo : relations.getAuthorPseudonyms()) {
			databaseDao.setAuthorPseudonym(authorPseudo.getLeft(), authorPseudo.getRight());
		}

		LOGGER.debug("Persisting series relations to universes migration...");
		try {
			rtuConverter.convert();
		} catch (SQLException e) {
			throw new SAXException("Failed to persist universe migrations", e);
		}
	}
}
