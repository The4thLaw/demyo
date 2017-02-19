package org.demyo.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Defines a dated price for an {@link Derivative}.
 */
@Entity
@Table(name = "DERIVATIVES_PRICES")
@IdClass(DerivativePriceId.class)
public class DerivativePrice extends AbstractPrice<DerivativePrice, Derivative> {
	/** The date at which the price was applicable. */
	@Column(name = "date")
	@Id
	@NotNull
	private Date date;

	/** The price the derivative was worth at the given date. */
	@Column(name = "price")
	@Min(0)
	@NotNull
	private BigDecimal price;

	/** The parent {@link Derivative} ID. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "derivative_id")
	@NotNull
	@Id
	private Derivative derivative;

	@Override
	public Date getDate() {
		return date;
	}

	@Override
	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public BigDecimal getPrice() {
		return price;
	}

	@Override
	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	/**
	 * Gets the parent {@link Derivative}.
	 * 
	 * @return the parent {@link Derivative}
	 */
	public Derivative getDerivative() {
		return derivative;
	}

	/**
	 * Sets the parent {@link Derivative}.
	 * 
	 * @param derivative the new parent {@link Derivative}
	 */
	public void setDerivative(Derivative derivative) {
		this.derivative = derivative;
	}

	@Override
	protected Derivative getModel() {
		return getDerivative();
	}

	@Override
	protected void setModel(Derivative model) {
		setDerivative(model);
	}
}
