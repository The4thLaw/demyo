package org.demyo.model;

import java.math.BigDecimal;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.demyo.model.util.AbstractModelComparator;

/**
 * Base class for models which have historised prices.
 *
 * @param <P> The price type
 * @param <M> The priced model type
 */
@MappedSuperclass
public abstract class AbstractPrice<P extends AbstractPrice<P, M>, M extends IModel> implements Comparable<P> {
	/** The date at which the price was applicable. */
	@Column(name = "date")
	@Id
	@NotNull
	private Date date;

	/** The price the model was worth at the given date. */
	@Column(name = "price")
	@Min(0)
	@NotNull
	private BigDecimal price;

	/**
	 * Gets the model to which this price is linked.
	 *
	 * @return The model
	 */
	protected abstract M getModel();

	/**
	 * Sets the reference to the model in the price.
	 *
	 * @param model The model to set
	 */
	protected abstract void setModel(M model);

	@Override
	public int compareTo(P o) {
		int comparison = 0;

		M model = getModel();
		if (model != null) {
			comparison = AbstractModelComparator.nullSafeComparison(model.getId(), o.getModel().getId());
		}
		if (comparison != 0) {
			return comparison;
		}

		comparison = AbstractModelComparator.nullSafeComparison(getDate(), o.getDate());
		if (comparison != 0) {
			return comparison;
		}

		comparison = AbstractModelComparator.nullSafeComparison(getPrice(), o.getPrice());

		return comparison;
	}

	/**
	 * Gets the date at which the price was applicable.
	 *
	 * @return the date at which the price was applicable
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * Sets the date at which the price was applicable.
	 *
	 * @param date the new date at which the price was applicable
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * Gets the price the album was worth at the given date.
	 *
	 * @return the price the album was worth at the given date
	 */
	public BigDecimal getPrice() {
		return price;
	}

	/**
	 * Sets the price the album was worth at the given date.
	 *
	 * @param price the new price the album was worth at the given date
	 */
	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	@Override
	public String toString() {
		return "AbstractPrice(model_id=" + (getModel() != null ? getModel().getId() : null) + ", date=" + getDate()
				+ ", price=" + getPrice() + ")";
	}

	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		if (!other.getClass().equals(getClass())) {
			return false;
		}

		AbstractPrice<?, ?> otherPrice = (AbstractPrice<?, ?>) other;
		EqualsBuilder builder = new EqualsBuilder();
		builder.append(getModel(), otherPrice.getModel());
		builder.append(getDate(), otherPrice.getDate());
		builder.append(getPrice(), otherPrice.getPrice());
		return builder.build();
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		builder.append(getModel());
		builder.append(getDate());
		builder.append(getPrice());
		return builder.build();
	}
}
