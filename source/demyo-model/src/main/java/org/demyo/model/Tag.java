package org.demyo.model;

import java.util.SortedSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.NamedSubgraph;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;

import org.hibernate.Hibernate;
import org.hibernate.annotations.SortComparator;

import org.demyo.model.util.AlbumAndSeriesComparator;
import org.demyo.model.util.DefaultOrder;
import org.demyo.model.util.StartsWithField;

/**
 * Represents a Tag.
 */
@Entity
@Table(name = "TAGS")
@DefaultOrder(expression = @DefaultOrder.Order(property = "name"))
@NamedEntityGraphs({
		@NamedEntityGraph(name = "Tag.forView", attributeNodes =
		{ @NamedAttributeNode(value = "taggedAlbums", subgraph = "Tag.Album") }, subgraphs = {
				@NamedSubgraph(name = "Tag.Album", attributeNodes =
				{ @NamedAttributeNode("series") }) }) })
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
	@SortComparator(AlbumAndSeriesComparator.class)
	private SortedSet<Album> taggedAlbums;

	/** The number of times this Tag has been used. */
	@Transient
	private Integer usageCount;

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
	 * Gets the Albums tagged with this Tag.
	 * 
	 * @return the Albums tagged with this Tag
	 */
	public SortedSet<Album> getTaggedAlbums() {
		return taggedAlbums;
	}

	/**
	 * Sets the Albums tagged with this Tag.
	 * 
	 * @param taggedAlbums the new Albums tagged with this Tag
	 */
	public void setTaggedAlbums(SortedSet<Album> taggedAlbums) {
		this.taggedAlbums = taggedAlbums;
	}

	/**
	 * Gets the number of times this Tag has been used.
	 * 
	 * @return the number of times this Tag has been used
	 */
	public Integer getUsageCount() {
		if (!Hibernate.isInitialized(taggedAlbums)) {
			return null;
		}

		if (usageCount == null) {
			return taggedAlbums.size();
		}

		return usageCount;
	}

	/**
	 * Sets the number of times this Tag has been used.
	 * 
	 * @param usageCount the new number of times this Tag has been used
	 */
	public void setUsageCount(Integer usageCount) {
		this.usageCount = usageCount;
	}
}
