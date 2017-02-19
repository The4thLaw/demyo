package org.demyo.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Specific class for the {@link DerivativePrice} ID.
 */
public class DerivativePriceId implements Serializable {
	private static final long serialVersionUID = 7283951787120783025L;

	private Long derivative;
	private Date date;

	/**
	 * Default constructor.
	 */
	public DerivativePriceId() {

	}

	/**
	 * Creates an ID based on the provided fields.
	 * 
	 * @param derivative The Derivative ID
	 * @param date The date for the price
	 */
	public DerivativePriceId(Long derivative, Date date) {
		this.derivative = derivative;
		this.date = date;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof DerivativePriceId)) {
			return false;
		}
		DerivativePriceId otherId = (DerivativePriceId) other;
		return derivative.equals(otherId.derivative) && date.equals(otherId.date);
	}

	@Override
	public int hashCode() {
		return derivative.hashCode() ^ date.hashCode();
	}

	@Override
	public String toString() {
		return "DerivativePriceId(derivative_id=" + derivative + ", date=" + date + ")";
	}
}
