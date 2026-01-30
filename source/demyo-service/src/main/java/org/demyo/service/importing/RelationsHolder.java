package org.demyo.service.importing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.xml.sax.Attributes;

public class RelationsHolder {
	private static final String FK_ALBUM_ID = "album_id";
	private static final String FK_READER_ID = "reader_id";
	private static final String FK_SERIES_ID = "series_id";

	private final List<Map<String, String>> seriesTaxons = new ArrayList<>();
	private final List<Map<String, String>> albumArtists = new ArrayList<>();
	private final List<Map<String, String>> albumWriters = new ArrayList<>();
	private final List<Map<String, String>> albumColorists = new ArrayList<>();
	private final List<Map<String, String>> albumInkers = new ArrayList<>();
	private final List<Map<String, String>> albumTranslators = new ArrayList<>();
	private final List<Map<String, String>> albumCoverArtists = new ArrayList<>();
	private final List<Map<String, String>> albumTaxons = new ArrayList<>();
	private final List<Map<String, String>> albumImages = new ArrayList<>();
	private final List<Pair<String, String>> authorPseudonyms = new ArrayList<>();
	private final List<Map<String, String>> derivativeImages = new ArrayList<>();
	private final List<Map<String, String>> readerFavouriteSeries = new ArrayList<>();
	private final List<Map<String, String>> readerFavouriteAlbums = new ArrayList<>();
	private final List<Map<String, String>> readerReadingList = new ArrayList<>();
	private final Map<String, List<Map<String, String>>> allRelations = new HashMap<>();

	public RelationsHolder() {
		allRelations.put("series_taxons", seriesTaxons);
		allRelations.put("albums_artists", albumArtists);
		allRelations.put("albums_writers", albumWriters);
		allRelations.put("albums_colorists", albumColorists);
		allRelations.put("albums_inkers", albumInkers);
		allRelations.put("albums_translators", albumTranslators);
		allRelations.put("albums_cover_artists", albumCoverArtists);
		allRelations.put("albums_taxons", albumTaxons);
		allRelations.put("albums_images", albumImages);
		allRelations.put("derivatives_images", derivativeImages);
		allRelations.put("readers_favourite_series", readerFavouriteSeries);
		allRelations.put("readers_favourite_albums", readerFavouriteAlbums);
		allRelations.put("readers_reading_list", readerReadingList);
	}

	private static Map<String, String> join(String col1, String val1, String col2, String val2) {
		HashMap<String, String> columns = new HashMap<>();
		columns.put(col1, val1);
		columns.put(col2, val2);
		return columns;
	}

	public void addSeriesTaxon(String seriesId, Attributes attributes) {
		seriesTaxons.add(join(FK_SERIES_ID, seriesId, "taxon_id", attributes.getValue("ref")));
	}

	public void addAlbumArtist(String albumId, Attributes attributes) {
		albumArtists.add(join(FK_ALBUM_ID, albumId, "artist_id", attributes.getValue("ref")));
	}

	public void addAlbumWriters(String albumId, Attributes attributes) {
		albumWriters.add(join(FK_ALBUM_ID, albumId, "writer_id", attributes.getValue("ref")));
	}

	public void addAlbumColorists(String albumId, Attributes attributes) {
		albumColorists.add(join(FK_ALBUM_ID, albumId, "colorist_id", attributes.getValue("ref")));
	}

	public void addAlbumInkers(String albumId, Attributes attributes) {
		albumInkers.add(join(FK_ALBUM_ID, albumId, "inker_id", attributes.getValue("ref")));
	}

	public void addAlbumTranslators(String albumId, Attributes attributes) {
		albumTranslators.add(join(FK_ALBUM_ID, albumId, "translator_id", attributes.getValue("ref")));
	}

	public void addAlbumCoverArtists(String albumId, Attributes attributes) {
		albumCoverArtists.add(join(FK_ALBUM_ID, albumId, "cover_artist_id", attributes.getValue("ref")));
	}

	public void addAlbumTag(String albumId, Attributes attributes) {
		albumTaxons.add(join(FK_ALBUM_ID, albumId, "tag_id", attributes.getValue("ref")));
	}

	public void addAlbumTaxon(String albumId, Attributes attributes) {
		albumTaxons.add(join(FK_ALBUM_ID, albumId, "taxon_id", attributes.getValue("ref")));
	}

	public void addAlbumImage(String albumId, Attributes attributes) {
		albumImages.add(join(FK_ALBUM_ID, albumId, "image_id", attributes.getValue("ref")));
	}

	public void addDerivativeImage(String derivativeId, Attributes attributes) {
		derivativeImages.add(join("derivative_id", derivativeId, "image_id", attributes.getValue("ref")));
	}

	public void addReaderFavouriteSeries(String readerId, Attributes attributes) {
		readerFavouriteSeries.add(join(FK_READER_ID, readerId, FK_SERIES_ID, attributes.getValue("ref")));
	}

	public void addReaderFavouriteAlbum(String readerId, Attributes attributes) {
		readerFavouriteAlbums.add(join(FK_READER_ID, readerId, FK_ALBUM_ID, attributes.getValue("ref")));
	}

	public void addReaderReadingList(String readerId, Attributes attributes) {
		readerReadingList.add(join(FK_READER_ID, readerId, FK_ALBUM_ID, attributes.getValue("ref")));
	}

	public void addAuthorPseudonym(String authorId, String pseudonymOfId) {
		authorPseudonyms.add(new ImmutablePair<>(authorId, pseudonymOfId));
	}

	public List<Pair<String,String>> getAuthorPseudonyms() {
		return authorPseudonyms;
	}

    public Map<String, List<Map<String, String>>> getAllRelations() {
        return allRelations;
    }
}
