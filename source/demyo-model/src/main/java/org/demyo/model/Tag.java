package org.demyo.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.demyo.model.util.DefaultOrder;
import org.demyo.model.util.StartsWithField;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Represents a Tag.
 */
@Entity
@Table(name = "TAGS")
@DefaultOrder(expression = @DefaultOrder.Order(property = "name"))
public class Tag extends AbstractModel {
	/** The name of the Tag. */
	@Column(name = "name")
	@NotBlank
	@StartsWithField
	private String name;

	/** The foreground colour. */
	@Column(name = "fgcolour")
	private String fgColour;

	/** The background colour. */
	@Column(name = "bgcolour")
	private String bgColour;

	/** The Albums tagged with this Tag. */
	@ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
	private List<Album> taggedAlbums;

	@Override
	public String getIdentifyingName() {
		return getName();
	}

	/**
	 * Gets the name of the Tag.
	 * 
	 * @return the name of the Tag
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the Tag.
	 * 
	 * @param name the new name of the Tag
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the foreground colour.
	 * 
	 * @return the foreground colour
	 */
	public String getFgColour() {
		return fgColour;
	}

	/**
	 * Sets the foreground colour.
	 * 
	 * @param fgColour the new foreground colour
	 */
	public void setFgColour(String fgColour) {
		this.fgColour = fgColour;
	}

	/**
	 * Gets the background colour.
	 * 
	 * @return the background colour
	 */
	public String getBgColour() {
		return bgColour;
	}

	/**
	 * Sets the background colour.
	 * 
	 * @param bgColour the new background colour
	 */
	public void setBgColour(String bgColour) {
		this.bgColour = bgColour;
	}

	/**
	 * Gets the number of times this Tag has been used.
	 * 
	 * @return the number of times this Tag has been used.
	 */
	@Transient
	public int getOccurrencesCount() {
		return taggedAlbums.size();
	}

	/**
	 * Gets the Albums tagged with this Tag.
	 * 
	 * @return the Albums tagged with this Tag
	 */
	public List<Album> getTaggedAlbums() {
		return taggedAlbums;
	}

	/**
	 * Sets the Albums tagged with this Tag.
	 * 
	 * @param taggedAlbums the new Albums tagged with this Tag
	 */
	public void setTaggedAlbums(List<Album> taggedAlbums) {
		this.taggedAlbums = taggedAlbums;
	}
}
