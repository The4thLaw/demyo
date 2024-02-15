package org.demyo.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotBlank;

import org.demyo.model.util.StartsWithField;

/**
 * A model which is identified by its name.
 */
@MappedSuperclass
public abstract class AbstractNamedModel extends AbstractModel {
	/** The name of this Model. */
	@Column(name = "name")
	@NotBlank
	@StartsWithField
	private String name;

	@Override
	public String getIdentifyingName() {
		return getName();
	}

	/**
	 * Gets the name of this Model.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of this Model.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

}
