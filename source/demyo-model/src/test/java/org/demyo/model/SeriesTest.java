package org.demyo.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.demyo.model.TestModelBuilders.albumByTitle;
import static org.demyo.model.TestModelBuilders.authorByName;
import static org.demyo.model.TestModelBuilders.seriesByName;
import static org.demyo.model.TestModelBuilders.tagByName;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * Tests for {@link Series}.
 */
public class SeriesTest {
	private static Series getSampleSeries() {
		Series s = seriesByName(1, "Sillage");

		Album album1 = albumByTitle(1, "À feu et à cendres");
		s.getAlbums()
				.add(album1);
		Album album2 = albumByTitle(2, "Collection privée");
		s.getAlbums()
				.add(album2);
		album2.setWishlist(true);

		Tag tagSf = tagByName(1, "science-fiction");
		album1.getTags()
				.add(tagSf);
		album2.getTags()
				.add(tagSf);
		album1.getTags()
				.add(tagByName(2, "jungle"));
		album2.getTags()
				.add(tagByName(3, "collection"));

		Author author1 = authorByName(1, "Jean-David", "Morvan");
		Author author2 = authorByName(2, "Philippe", "Buchet");
		Author author3 = authorByName(3, "Pierre-Mony", "Chan");
		album1.getWriters()
				.add(author1);
		album1.getArtists()
				.add(author2);
		album2.getWriters()
				.add(author3);
		album2.getArtists()
				.add(author3);

		return s;
	}

	/**
	 * Tests {@link Series#getAlbumTags()}.
	 */
	@Test
	public void getAlbumTags() {
		Series s = getSampleSeries();

		List<Tag> tags = new ArrayList<>(s.getAlbumTags());

		assertThat(tags).hasSize(3);
		assertThat(tags).element(0)
				.hasFieldOrPropertyWithValue("name", "collection");
		assertThat(tags).element(1)
				.hasFieldOrPropertyWithValue("name", "jungle");
		assertThat(tags).element(2)
				.hasFieldOrPropertyWithValue("name", "science-fiction");
	}

	/**
	 * Tests {@link Series#getAlbumArtists()}.
	 */
	@Test
	public void getAlbumArtists() {
		Series s = getSampleSeries();

		List<Author> artists = new ArrayList<>(s.getAlbumArtists());

		assertThat(artists).hasSize(2);
		assertThat(artists).element(0)
				.hasFieldOrPropertyWithValue("name", "Buchet");
		assertThat(artists).element(1)
				.hasFieldOrPropertyWithValue("name", "Chan");
	}

	/**
	 * Tests {@link Series#getAlbumWriters()}.
	 */
	@Test
	public void getAlbumWriters() {
		Series s = getSampleSeries();

		List<Author> writers = new ArrayList<>(s.getAlbumWriters());

		assertThat(writers).hasSize(2);
		assertThat(writers).element(0)
				.hasFieldOrPropertyWithValue("name", "Morvan");
		assertThat(writers).element(1)
				.hasFieldOrPropertyWithValue("name", "Chan");
	}

	/**
	 * Tests {@link Series#getOwnedAlbumCount()}.
	 */
	@Test
	public void getOwnedAlbumCount() {
		Series s = getSampleSeries();

		assertThat(s.getOwnedAlbumCount()).isEqualTo(1);
	}
}
