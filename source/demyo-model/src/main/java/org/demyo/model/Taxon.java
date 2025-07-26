package org.demyo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonView;

import org.demyo.model.enums.TaxonType;
import org.demyo.model.util.DefaultOrder;

/**
 * Represents a Taxon.
 */
@Entity
@Table(name = "TAXONS")
@DefaultOrder(expression = @DefaultOrder.Order(property = "name"))
public class Taxon extends AbstractNamedModel {
	/** The type of taxon. */
	@Enumerated(EnumType.STRING)
	@Column(name = "taxon_type")
	@JsonView(ModelView.Minimal.class)
	private TaxonType type;

	/** The foreground colour. */
	@Column(name = "fgcolour")
	private String fgColour;

	/** The background colour. */
	@Column(name = "bgcolour")
	private String bgColour;

	/** The description for this taxon. */
	@Column(name = "description")
	private String description;

	/** The number of times this Taxon has been used. */
	@Transient
	private Integer usageCount;

	/**
	 * Gets the type of taxon.
	 *
	 * @return the type
	 */
	public TaxonType getType() {
		return type;
	}

	/**
	 * Sets the type of taxon.
	 *
	 * @param type the new type
	 */
	public void setType(TaxonType type) {
		this.type = type;
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
	 * Gets the description for this taxon.
	 *
	 * @return the description for this taxon
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description for this taxon.
	 *
	 * @param description the new description for this taxon
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the number of times this Taxon has been used.
	 *
	 * @return the number of times this Taxon has been used
	 */
	@JsonView(ModelView.Basic.class)
	public Integer getUsageCount() {
		return usageCount;
	}

	/**
	 * Sets the number of times this Taxon has been used.
	 *
	 * @param usageCount the new number of times this Taxon has been used
	 */
	public void setUsageCount(Integer usageCount) {
		this.usageCount = usageCount;
	}
}
