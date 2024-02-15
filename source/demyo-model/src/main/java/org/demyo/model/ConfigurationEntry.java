package org.demyo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonBackReference;

import org.demyo.model.util.DefaultOrder;

/**
 * Represents a type of binding.
 */
@Entity
@Table(name = "CONFIGURATION")
@DefaultOrder(expression = @DefaultOrder.Order(property = "key"))
public class ConfigurationEntry extends AbstractModel {
	/** The key for the configuration property. */
	@Column(name = "config_key")
	@NotBlank
	private String key;

	/** The value for the configuration property. */
	@Column(name = "config_value")
	private String value;

	/** The {@link Reader} owning this configuration entry. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reader_id")
	@JsonBackReference
	private Reader reader;

	@Override
	public String getIdentifyingName() {
		return getKey();
	}

	/**
	 * Gets the key for the configuration property.
	 *
	 * @return the key for the configuration property
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Sets the key for the configuration property.
	 *
	 * @param key the new key for the configuration property
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * Gets the value for the configuration property.
	 *
	 * @return the value for the configuration property
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value for the configuration property.
	 *
	 * @param value the new value for the configuration property
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Gets the {@link Reader} owning this configuration entry.
	 *
	 * @return the {@link Reader} owning this configuration entry
	 */
	public Reader getReader() {
		return reader;
	}

	/**
	 * Sets the {@link Reader} owning this configuration entry.
	 *
	 * @param reader the new {@link Reader} owning this configuration entry
	 */
	public void setReader(Reader reader) {
		this.reader = reader;
	}
}
