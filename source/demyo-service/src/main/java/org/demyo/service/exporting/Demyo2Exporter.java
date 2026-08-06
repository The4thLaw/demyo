package org.demyo.service.exporting;

import java.nio.file.Path;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.the4thlaw.commons.exception.CommonException;
import org.the4thlaw.commons.services.exporting.BaseXmlExporter;
import org.the4thlaw.commons.services.exporting.IExporter;
import org.the4thlaw.commons.services.exporting.ManyToManyRelation;
import org.the4thlaw.commons.services.io.IDirectoryService;

import org.demyo.common.config.SystemConfiguration;
import org.demyo.dao.IRawSQLDao;

/**
 * {@link IExporter} using the native Demyo 2 format.
 */
@Component
public class Demyo2Exporter extends BaseXmlExporter {
	private static final String IMAGE_KEY = "IMAGE_ID";
	private static final String SERIES_KEY = "SERIES_ID";
	private static final String READER_KEY = "READER_ID";
	private static final String ALBUM_KEY = "ALBUM_ID";

	public Demyo2Exporter(IRawSQLDao rawSQLDao, IDirectoryService directoryService) {
		super("demyo", SystemConfiguration.getInstance().getVersion(), "dea", directoryService, rawSQLDao);
	}

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public Path export() throws CommonException {
		// We just wrap with a transaction
		return super.export();
	}

	@Override
	protected void exportModels(XMLStreamWriter xsw) throws XMLStreamException {
		exportModel(xsw, "images", "image", "IMAGES");
		exportModel(xsw, "publishers", "publisher", "PUBLISHERS");
		exportModel(xsw, "collections", "collection", "COLLECTIONS");
		exportModel(xsw, "bindings", "binding", "BINDINGS");
		exportModel(xsw, "authors", "author", "AUTHORS");
		exportModel(xsw, "taxons", "taxon", "TAXONS");
		exportModel(xsw, "book_types", "book_type", "BOOK_TYPES");

		exportModel(xsw, "universes", "universe", "UNIVERSES",
				new ManyToManyRelation("universe-images", "universe-image", "UNIVERSE_ID", IMAGE_KEY,
						databaseDao.getRawRecords("UNIVERSES_IMAGES")));

		exportModel(xsw, "series-list", "series", "SERIES",
				new ManyToManyRelation("series-taxons", "series-taxon", SERIES_KEY, "TAXON_ID",
						databaseDao.getRawRecords("SERIES_TAXONS")));

		// Albums
		exportModel(xsw, "albums", "album", "ALBUMS",
				new ManyToManyRelation("writers", "writer", ALBUM_KEY, "WRITER_ID",
						databaseDao.getRawRecords("ALBUMS_WRITERS")),
				new ManyToManyRelation("artists", "artist", ALBUM_KEY, "ARTIST_ID",
						databaseDao.getRawRecords("ALBUMS_ARTISTS")),
				new ManyToManyRelation("colorists", "colorist", ALBUM_KEY, "COLORIST_ID",
						databaseDao.getRawRecords("ALBUMS_COLORISTS")),
				new ManyToManyRelation("inkers", "inker", ALBUM_KEY, "INKER_ID",
						databaseDao.getRawRecords("ALBUMS_INKERS")),
				new ManyToManyRelation("translators", "translator", ALBUM_KEY, "TRANSLATOR_ID",
						databaseDao.getRawRecords("ALBUMS_TRANSLATORS")),
				new ManyToManyRelation("cover-artists", "cover-artist", ALBUM_KEY, "COVER_ARTIST_ID",
						databaseDao.getRawRecords("ALBUMS_COVER_ARTISTS")),
				new ManyToManyRelation("album-taxons", "album-taxon", ALBUM_KEY, "TAXON_ID",
						databaseDao.getRawRecords("ALBUMS_TAXONS")),
				new ManyToManyRelation("album-images", "album-image", ALBUM_KEY, IMAGE_KEY,
						databaseDao.getRawRecords("ALBUMS_IMAGES")));

		exportModel(xsw, "album_prices", "album_price", "ALBUMS_PRICES");
		exportModel(xsw, "borrowers", "borrower", "BORROWERS");
		exportModel(xsw, "loan-history", "loan", "ALBUMS_BORROWERS");
		exportModel(xsw, "derivative_types", "derivative_type", "DERIVATIVE_TYPES");
		exportModel(xsw, "sources", "source", "SOURCES");

		// Derivatives
		exportModel(xsw, "derivatives", "derivative", "DERIVATIVES",
				new ManyToManyRelation("derivative-images", "derivative-image", "DERIVATIVE_ID", IMAGE_KEY,
						databaseDao.getRawRecords("DERIVATIVES_IMAGES")));

		exportModel(xsw, "derivative_prices", "derivative_price", "DERIVATIVES_PRICES");

		// Readers
		exportModel(xsw, "readers", "reader", "READERS",
				new ManyToManyRelation("favourite-series-list", "favourite-series", READER_KEY, SERIES_KEY,
						databaseDao.getRawRecords("READERS_FAVOURITE_SERIES")),
				new ManyToManyRelation("favourite-albums", "favourite-album", READER_KEY, ALBUM_KEY,
						databaseDao.getRawRecords("READERS_FAVOURITE_ALBUMS")),
				new ManyToManyRelation("reading-list", "reading-list-entry", READER_KEY, ALBUM_KEY,
						databaseDao.getRawRecords("READERS_READING_LIST")));

		exportModel(xsw, "configuration", "configuration-entry", "CONFIGURATION");
	}

}
