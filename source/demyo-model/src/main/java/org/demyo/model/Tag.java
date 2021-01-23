package org.demyo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonView;

import org.demyo.model.util.DefaultOrder;
import org.demyo.model.util.StartsWithField;

/**
 * Represents a Tag.
 */
@Entity
@Table(name = "TAGS")
@DefaultOrder(expression = @DefaultOrder.Order(property = "name"))
public class Tag extends AbstractModel {
	/** The name of the Tag. */
	@Column(name = "name")
	@NotBlank
	@StartsWithField
	private String name;

	/** The foreground colour. */
	@Column(name = "fgcolour")
	private String fgColour;

	/** The background colour. */
	@Column(name = "bgcolour")
	private String bgColour;

	/** The number of times this Tag has been used. */
	@Transient
	private Integer usageCount;

	@Override
	public String getIdentifyingName() {
		return getName();
	}

	/**
	 * Gets the name of the Tag.
	 * 
	 * @return the name of the Tag
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the Tag.
	 * 
	 * @param name the new name of the Tag
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the foreground colour.
	 * 
	 * @return the foreground colour
	 */
	@JsonView(ModelView.Basic.class)
	public String getFgColour() {
		return fgColour;
	}

	/**
	 * Sets the foreground colour.
	 * 
	 * @param fgColour the new foreground colour
	 */
	public void setFgColour(String fgColour) {
		this.fgColour = fgColour;
	}

	/**
	 * Gets the background colour.
	 * 
	 * @return the background colour
	 */
	@JsonView(ModelView.Basic.class)
	public String getBgColour() {
		return bgColour;
	}

	/**
	 * Sets the background colour.
	 * 
	 * @param bgColour the new background colour
	 */
	public void setBgColour(String bgColour) {
		this.bgColour = bgColour;
	}

	/**
	 * Gets the number of times this Tag has been used.
	 * 
	 * @return the number of times this Tag has been used
	 */
	@JsonView(ModelView.Basic.class)
	public Integer getUsageCount() {
		return usageCount;
	}

	/**
	 * Sets the number of times this Tag has been used.
	 * 
	 * @param usageCount the new number of times this Tag has been used
	 */
	public void setUsageCount(Integer usageCount) {
		this.usageCount = usageCount;
	}
}
