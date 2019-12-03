package org.demyo.model.beans;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import org.demyo.model.Album;
import org.demyo.model.ModelView;
import org.demyo.model.projections.IAuthorAlbum;
import org.demyo.model.util.AlbumAndSeriesComparator;

/**
 * Represents all {@link Album}s from a given Author, with indexes of which albums the author participated to in which
 * roles.
 */
public class AuthorAlbums {
	private final Set<Long> asArtist = new HashSet<>();
	private final Set<Long> asColorist = new HashSet<>();
	private final Set<Long> asInker = new HashSet<>();
	private final Set<Long> asTranslator = new HashSet<>();
	private final Set<Long> asWriter = new HashSet<>();
	private final Set<Album> albums = new TreeSet<>(new AlbumAndSeriesComparator());

	private static void add(Set<Long> set, Long id) {
		if (id != null) {
			set.add(id);
		}
	}

	/**
	 * Adds an {@link IAuthorAlbum} to the set of {@link Album}s considered by this aggregate.
	 * 
	 * @param album The album to add.
	 */
	public void addWork(IAuthorAlbum album) {
		add(asArtist, album.getAsArtist());
		add(asColorist, album.getAsColorist());
		add(asInker, album.getAsInker());
		add(asTranslator, album.getAsTranslator());
		add(asWriter, album.getAsWriter());
	}

	/**
	 * Gets the internal IDs of all considered {@link Album}s, regardless of the role.
	 * 
	 * @return The IDs
	 */
	@JsonIgnore
	public Set<Long> getAllAlbumsIds() {
		Set<Long> ids = new HashSet<>();
		ids.addAll(asArtist);
		ids.addAll(asColorist);
		ids.addAll(asInker);
		ids.addAll(asTranslator);
		ids.addAll(asWriter);
		return ids;
	}

	/**
	 * Sets the full {@link Album} entities.
	 * 
	 * @param albums The albums
	 */
	public void setAlbums(Collection<Album> albums) {
		this.albums.clear();
		this.albums.addAll(albums);
	}

	/**
	 * Gets the internal IDs of the {@link Album}s to which the Author participated to as artist.
	 * 
	 * @return The IDs
	 */
	@JsonView(ModelView.Basic.class)
	public Set<Long> getAsArtist() {
		return asArtist;
	}

	/**
	 * Gets the internal IDs of the {@link Album}s to which the Author participated to as colorist.
	 * 
	 * @return The IDs
	 */
	@JsonView(ModelView.Basic.class)
	public Set<Long> getAsColorist() {
		return asColorist;
	}

	/**
	 * Gets the internal IDs of the {@link Album}s to which the Author participated to as inker.
	 * 
	 * @return The IDs
	 */
	@JsonView(ModelView.Basic.class)
	public Set<Long> getAsInker() {
		return asInker;
	}

	/**
	 * Gets the internal IDs of the {@link Album}s to which the Author participated to as translator.
	 * 
	 * @return The IDs
	 */
	@JsonView(ModelView.Basic.class)
	public Set<Long> getAsTranslator() {
		return asTranslator;
	}

	/**
	 * Gets the internal IDs of the {@link Album}s to which the Author participated to as writer.
	 * 
	 * @return The IDs
	 */
	@JsonView(ModelView.Basic.class)
	public Set<Long> getAsWriter() {
		return asWriter;
	}

	/**
	 * Gets the full {@link Album} entities. The list is sorted.
	 * 
	 * @return The {@link Album}s
	 */
	@JsonView(ModelView.Basic.class)
	public Collection<Album> getAlbums() {
		return albums;
	}
}
