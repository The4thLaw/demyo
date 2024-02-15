package org.demyo.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.hibernate.annotations.SortComparator;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;

import org.demyo.model.config.ApplicationConfiguration;
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
@NamedEntityGraph(name = "Reader.forView", attributeNodes =
{ @NamedAttributeNode("configurationEntries") })
public class Reader extends AbstractNamedModel {
	/** The preferred colour of the reader. */
	@Column(name = "colour")
	@JsonView(ModelView.Basic.class)
	private String colour;

	/** The configuration entries for the reader. */
	@OneToMany(mappedBy = "reader", fetch = FetchType.LAZY)
	@JsonManagedReference
	private Set<ConfigurationEntry> configurationEntries;

	/** The configuration for the reader. */
	@Transient
	private ApplicationConfiguration configuration;

	/** The reader's favourite series. */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "readers_favourite_series", joinColumns = @JoinColumn(name = "reader_id"), //
			inverseJoinColumns = @JoinColumn(name = "series_id"))
	@SortComparator(IdentifyingNameComparator.class)
	private SortedSet<Series> favouriteSeries;

	/** The reader's favourite albums. */
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

	/**
	 * Gets the preferred colour of the reader.
	 *
	 * @return the preferred colour of the reader
	 */
	public String getColour() {
		return colour;
	}

	/**
	 * Sets the preferred colour of the reader.
	 *
	 * @param colour the new preferred colour of the reader
	 */
	public void setColour(String colour) {
		this.colour = colour;
	}

	/**
	 * Gets the configuration for the reader.
	 *
	 * @return the configuration for the reader
	 */
	public ApplicationConfiguration getConfiguration() {
		if (configuration == null) {
			throw new IllegalStateException("The configuration is not initialized");
		}
		return configuration;
	}

	/**
	 * Initializes the configuration for the reader.
	 *
	 * @param globalConfig The global configuration parameters to use as base.
	 */
	public void initializeConfiguration(Map<String, String> globalConfig) {
		Map<String, String> configurationValues = new HashMap<>(globalConfig);
		for (ConfigurationEntry entry : configurationEntries) {
			configurationValues.put(entry.getKey(), entry.getValue());
		}
		configuration = new ApplicationConfiguration(configurationValues);
	}

	/**
	 * Gets the configuration entries for the reader.
	 *
	 * @return the configuration entries for the reader
	 */
	public Set<ConfigurationEntry> getConfigurationEntries() {
		return configurationEntries;
	}

	/**
	 * Sets the configuration entries for the reader.
	 *
	 * @param configurationEntries the new configuration entries for the reader
	 */
	public void setConfigurationEntries(Set<ConfigurationEntry> configurationEntries) {
		this.configurationEntries = configurationEntries;
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
