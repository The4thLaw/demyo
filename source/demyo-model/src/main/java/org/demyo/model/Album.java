package org.demyo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.demyo.model.util.DefaultOrder;

// TODO: Auto-generated Javadoc
// TODO: far from complete
// TODO: order by is not complete
// TODO: when saving this entity, it should at least have a title or a Series.
/*
 * TODO: We will need two ways to order albums
 * 
 * - One for the index, where the work is done by the database: order by (case when series_id is null then
 * album.title else series.title), album.number or something like that
 * 
 * - One in Java to follow the same logic, but work on datasets retrieved e.g. from the tags, the authors, etc.
 * 
 * Or perhaps the database could be always used, but the albums would be fetched by separate queries and put in
 * transient fields
 * 
 * To be determined: does Hibernate allow to use direct SQL order queries
 */
/**
 * The Class Album.
 */
@Entity
@Table(name = "ALBUMS")
@DefaultOrder(expression = @DefaultOrder.Order(property = "name"))
public class Album extends AbstractModel {
	/** The parent {@link Series}. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "series_id")
	private Series series;

	/** The title. */
	@Column(name = "title")
	private String title;

	@Override
	public String getIdentifyingName() {
		return getTitle();
	}

	/**
	 * Gets the parent {@link Series}.
	 * 
	 * @return the parent {@link Series}
	 */
	public Series getSeries() {
		return series;
	}

	/**
	 * Sets the parent {@link Series}.
	 * 
	 * @param series the new parent {@link Series}
	 */
	public void setSeries(Series series) {
		this.series = series;
	}

	/**
	 * Gets the title.
	 * 
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the title.
	 * 
	 * @param title the new title
	 */
	public void setTitle(String title) {
		this.title = title;
	}
}
