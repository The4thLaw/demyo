package org.demyo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

// TODO: far from complete
/**
 * The Class Album.
 */
@Entity
@Table(name = "ALBUMS")
public class Album extends AbstractModel {
	/** The parent {@link Series}. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "series_id")
	private Series series;

	/** The cycle. */
	@Column(name = "cycle")
	private Integer cycle;

	/** The number. */
	@Column(name = "number")
	private Float number;

	/** The number suffix. */
	@Column(name = "number_suffix")
	private String numberSuffix;

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
	 * Gets the cycle.
	 * 
	 * @return the cycle
	 */
	public Integer getCycle() {
		return cycle;
	}

	/**
	 * Sets the cycle.
	 * 
	 * @param cycle the new cycle
	 */
	public void setCycle(Integer cycle) {
		this.cycle = cycle;
	}

	/**
	 * Gets the number.
	 * 
	 * @return the number
	 */
	public Float getNumber() {
		return number;
	}

	/**
	 * Sets the number.
	 * 
	 * @param number the new number
	 */
	public void setNumber(Float number) {
		this.number = number;
	}

	/**
	 * Gets the number suffix.
	 * 
	 * @return the number suffix
	 */
	public String getNumberSuffix() {
		return numberSuffix;
	}

	/**
	 * Sets the number suffix.
	 * 
	 * @param numberSuffix the new number suffix
	 */
	public void setNumberSuffix(String numberSuffix) {
		this.numberSuffix = numberSuffix;
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
