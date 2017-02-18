package org.demyo.model;

import java.util.SortedSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.demyo.model.util.AlbumAndSeriesComparator;
import org.demyo.model.util.DefaultOrder;
import org.demyo.model.util.StartsWithField;
import org.hibernate.annotations.SortComparator;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Represents a type of binding.
 */
@Entity
@Table(name = "BINDINGS")
@DefaultOrder(expression = @DefaultOrder.Order(property = "name"))
@NamedEntityGraph(name = "Binding.forView", attributeNodes = { @NamedAttributeNode("albums") })
public class Binding extends AbstractModel {
	/** The name of the Binding. */
	@Column(name = "name")
	@NotBlank
	@StartsWithField
	private String name;
	/** The albums which use this binding. */
	@OneToMany(mappedBy = "binding", fetch = FetchType.LAZY)
	@SortComparator(AlbumAndSeriesComparator.class)
	private SortedSet<Album> albums;

	@Override
	public String getIdentifyingName() {
		return getName();
	}

	/**
	 * Gets the name of the Binding.
	 * 
	 * @return the name of the Binding
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the Binding.
	 * 
	 * @param name
	 *            the new name of the Binding
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the albums which use this binding.
	 *
	 * @return the albums which use this binding
	 */
	public SortedSet<Album> getAlbums() {
		return albums;
	}

	/**
	 * Sets the albums which use this binding.
	 *
	 * @param albums
	 *            the new albums which use this binding
	 */
	public void setAlbums(SortedSet<Album> albums) {
		this.albums = albums;
	}
}
