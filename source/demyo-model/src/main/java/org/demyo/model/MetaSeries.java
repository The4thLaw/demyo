package org.demyo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.Table;

/**
 * A view of series names where albums without series are also listed (in that case, the title becomes the series name).
 * This is used to paginate lists of albums in a meaningful manner. The ID is positive for series, negative for albums.
 * 
 * @deprecated To be removed
 */
@Deprecated
// TODO [Vue] remove this and the view
@Entity
@Table(name = "V_META_SERIES")
@NamedEntityGraph(name = "MetaSeries.forIndex", attributeNodes = { @NamedAttributeNode("series"),
		@NamedAttributeNode(value = "album") })
public class MetaSeries extends AbstractModel {
	/** The {@link Series}. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "series_id")
	private Series series;

	/** The {@link Album}. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "album_id")
	private Album album;

	/** The name of the Series, or of the Album if the Album has no Series. */
	@Column(name = "meta_name")
	private String name;

	@Override
	public String getIdentifyingName() {
		return getName();
	}

	/**
	 * Gets the {@link Series}.
	 * 
	 * @return the {@link Series}
	 */
	public Series getSeries() {
		return series;
	}

	/**
	 * Gets the {@link Album}.
	 * 
	 * @return the {@link Album}
	 */
	public Album getAlbum() {
		return album;
	}

	/**
	 * Gets the name of the Series, or of the Album if the Album has no Series.
	 * 
	 * @return the name of the Series, or of the Album if the Album has no Series
	 */
	public String getName() {
		return name;
	}
}
