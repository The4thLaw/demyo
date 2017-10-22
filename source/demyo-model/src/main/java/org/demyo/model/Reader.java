package org.demyo.model;

import java.util.SortedSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.SortComparator;
import org.hibernate.validator.constraints.NotBlank;

import org.demyo.model.util.AlbumComparator;
import org.demyo.model.util.DefaultOrder;
import org.demyo.model.util.IdentifyingNameComparator;

/**
 * Represent Readers. Readers are mainly characterized by their reading list.
 * 
 * @since Demyo 2.1.
 */
@Entity
@Table(name = "READERS")
@DefaultOrder(expression = @DefaultOrder.Order(property = "name"))
// TODO [P2]: loans
public class Reader extends AbstractModel {
	/** The name. */
	@Column(name = "name")
	@NotBlank
	private String name;

	/** The preferred colour of the user. */
	@Column(name = "colour")
	private String colour;

	/** The reader's favourite series. */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "readers_favourite_series", joinColumns = @JoinColumn(name = "reader_id"), //
			inverseJoinColumns = @JoinColumn(name = "series_id"))
	@SortComparator(IdentifyingNameComparator.class)
	private SortedSet<Series> favouriteSeries;

	/** The favourite albums. */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "readers_favourite_albums", joinColumns = @JoinColumn(name = "reader_id"), //
			inverseJoinColumns = @JoinColumn(name = "album_id"))
	@SortComparator(AlbumComparator.class)
	private SortedSet<Album> favouriteAlbums;

	/** The reading list. */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "readers_reading_list", joinColumns = @JoinColumn(name = "reader_id"), //
			inverseJoinColumns = @JoinColumn(name = "album_id"))
	@SortComparator(AlbumComparator.class)
	private SortedSet<Album> readingList;

	@Override
	public String getIdentifyingName() {
		return name;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the preferred colour of the user.
	 *
	 * @return the preferred colour of the user
	 */
	public String getColour() {
		return colour;
	}

	/**
	 * Sets the preferred colour of the user.
	 *
	 * @param colour the new preferred colour of the user
	 */
	public void setColour(String colour) {
		this.colour = colour;
	}

	/**
	 * Gets the reader's favourite series.
	 *
	 * @return the reader's favourite series
	 */
	public SortedSet<Series> getFavouriteSeries() {
		return favouriteSeries;
	}

	/**
	 * Sets the reader's favourite series.
	 *
	 * @param favouriteSeries the new reader's favourite series
	 */
	public void setFavouriteSeries(SortedSet<Series> favouriteSeries) {
		this.favouriteSeries = favouriteSeries;
	}

	/**
	 * Gets the favourite albums.
	 *
	 * @return the favourite albums
	 */
	public SortedSet<Album> getFavouriteAlbums() {
		return favouriteAlbums;
	}

	/**
	 * Sets the favourite albums.
	 *
	 * @param favouriteAlbums the new favourite albums
	 */
	public void setFavouriteAlbums(SortedSet<Album> favouriteAlbums) {
		this.favouriteAlbums = favouriteAlbums;
	}

	/**
	 * Gets the reading list.
	 *
	 * @return the reading list
	 */
	public SortedSet<Album> getReadingList() {
		return readingList;
	}

	/**
	 * Sets the reading list.
	 *
	 * @param readingList the new reading list
	 */
	public void setReadingList(SortedSet<Album> readingList) {
		this.readingList = readingList;
	}
}
