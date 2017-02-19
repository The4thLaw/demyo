package org.demyo.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.MappedSuperclass;

import org.demyo.model.util.AbstractModelComparator;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Base class for models which have historised prices.
 * 
 * @param <P> The price type
 * @param <M> The priced model type
 */
@MappedSuperclass
public abstract class AbstractPrice<P extends AbstractPrice<P, M>, M extends IModel> implements Comparable<P> {
	/**
	 * Gets the model to which this price is linked.
	 * 
	 * @return The model.
	 */
	protected abstract M getModel();

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
	public abstract Date getDate();

	/**
	 * Sets the date at which the price was applicable.
	 * 
	 * @param date the new date at which the price was applicable
	 */
	public abstract void setDate(Date date);

	/**
	 * Gets the price the album was worth at the given date.
	 * 
	 * @return the price the album was worth at the given date
	 */
	public abstract BigDecimal getPrice();

	/**
	 * Sets the price the album was worth at the given date.
	 * 
	 * @param price the new price the album was worth at the given date
	 */
	public abstract void setPrice(BigDecimal price);

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
