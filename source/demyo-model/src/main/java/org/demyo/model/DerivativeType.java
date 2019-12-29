package org.demyo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import org.demyo.model.util.DefaultOrder;
import org.demyo.model.util.StartsWithField;

/**
 * Represents a type of Derivative.
 */
@Entity
@Table(name = "DERIVATIVE_TYPES")
@DefaultOrder(expression = @DefaultOrder.Order(property = "name"))
public class DerivativeType extends AbstractModel {
	/** The name of the Derivative Type. */
	@Column(name = "name")
	@NotBlank
	@StartsWithField
	private String name;

	@Override
	public String getIdentifyingName() {
		return getName();
	}

	/**
	 * Gets the name of the Derivative Type.
	 * 
	 * @return the name of the Derivative Type
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the Derivative Type.
	 * 
	 * @param name the new name of the Derivative Type
	 */
	public void setName(String name) {
		this.name = name;
	}
}
