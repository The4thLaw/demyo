package org.demyo.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * Defines a dated price for an {@link Derivative}.
 */
@Entity
@Table(name = "DERIVATIVES_PRICES")
@IdClass(DerivativePriceId.class)
public class DerivativePrice extends AbstractPrice<DerivativePrice, Derivative> {
	/** The parent {@link Derivative} ID. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "derivative_id")
	@NotNull
	@Id
	@JsonBackReference
	private Derivative derivative;

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
