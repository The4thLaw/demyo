package org.demyo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.demyo.model.util.DefaultOrder;
import org.demyo.model.util.StartsWithField;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Represents a type of binding.
 */
@Entity
@Table(name = "BINDINGS")
@DefaultOrder(expression = @DefaultOrder.Order(property = "name"))
public class Binding extends AbstractModel {
	/** The name of the Binding. */
	@Column(name = "name")
	@NotBlank
	@StartsWithField
	private String name;

	@Override
	public String getIdentifyingName() {
		return getName();
	}

	/**
	 * Gets the name of the Binding.
	 * 
	 * @return the name of the Binding
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the Binding.
	 * 
	 * @param name the new name of the Binding
	 */
	public void setName(String name) {
		this.name = name;
	}
}
