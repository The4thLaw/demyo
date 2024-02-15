package org.demyo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonView;

import org.demyo.model.util.DefaultOrder;

/**
 * Represents a Tag.
 */
@Entity
@Table(name = "TAGS")
@DefaultOrder(expression = @DefaultOrder.Order(property = "name"))
public class Tag extends AbstractNamedModel {
	/** The foreground colour. */
	@Column(name = "fgcolour")
	private String fgColour;

	/** The background colour. */
	@Column(name = "bgcolour")
	private String bgColour;

	/** The description for this tag. */
	@Column(name = "description")
	private String description;

	/** The number of times this Tag has been used. */
	@Transient
	private Integer usageCount;

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
	 * Gets the description for this tag.
	 *
	 * @return the description for this tag
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description for this tag.
	 *
	 * @param description the new description for this tag
	 */
	public void setDescription(String description) {
		this.description = description;
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
