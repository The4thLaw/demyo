package org.demyo.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.demyo.model.constraints.ISBN;

import org.hibernate.validator.constraints.NotBlank;

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
	@NotBlank
	private String title;

	/** The owning {@link Publisher}. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "publisher_id")
	@NotNull
	private Publisher publisher;

	/** The owning {@link Collection}. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "collection_id")
	private Collection collection;

	/** The date of first edition. */
	@Column(name = "first_edition")
	private Date firstEditionDate;

	/** The date of current edition. */
	@Column(name = "this_edition")
	private Date currentEditionDate;

	/** The ISBN. */
	@Column(name = "isbn")
	@ISBN
	private String isbn;

	/** The date of acquisition. */
	@Column(name = "acquisition_date")
	private Date acquisitionDate;

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
