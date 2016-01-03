package org.demyo.model;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * Base interface for all standard Demyo models.
 * 
 * @author Xavier 'Xr' Dalem
 * @version $Revision$
 */
public interface IModel {
	/**
	 * Gets the model ID.
	 * 
	 * @return The model ID.
	 */
	@JsonView(ModelView.Minimal.class)
	Long getId();

	/**
	 * Sets the model ID.
	 * 
	 * @param id The model ID.
	 */
	void setId(Long id);

	/**
	 * Gets a human-readable identifier for this model. It can be a field, a composition of fields, or anything of
	 * that kind. Preferably not dependent on links to other entities.
	 * 
	 * @return A string representing the object.
	 */
	@JsonView(ModelView.Minimal.class)
	String getIdentifyingName();
}
