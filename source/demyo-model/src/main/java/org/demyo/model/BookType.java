package org.demyo.model;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import org.demyo.model.enums.ModelField;
import org.demyo.model.enums.TranslationLabelType;
import org.demyo.model.util.DefaultOrder;

/**
 * Represents book types. Book types are an optional entity allowing to manage more than just comics.
 */
@Entity
@Table(name = "BOOK_TYPES")
@DefaultOrder(expression = @DefaultOrder.Order(property = "name"))
public class BookType extends AbstractNamedModel {
	private static final String FIELD_DELIMITOR = ",";

	/** The default name for the lone book type when book type management is disabled. */
	public static final String DEFAULT_NAME = "__DEFAULT__";

	/** The type of label to use for this type. */
	@Column(name = "label_type")
	@Enumerated(EnumType.STRING)
	private TranslationLabelType labelType;

	/** The fields to hide from this book type. */
	@Column(name = "field_config")
	private String fieldConfig;

	/** The description for this book type. */
	@Column(name = "description")
	private String description;

	/**
	 * Gets the type of label to use for this type.
	 *
	 * @return the type of label to use for this type
	 */
	public TranslationLabelType getLabelType() {
		return labelType;
	}

	/**
	 * Sets the type of label to use for this type.
	 *
	 * @param labelType the type of label to use for this type
	 */
	public void setLabelType(TranslationLabelType labelType) {
		this.labelType = labelType;
	}

	/**
	 * Gets the unstructured field configuration.
	 *
	 * @return the unstructured field configuration.
	 */
	/* package */ String getFieldConfig() {
		return fieldConfig;
	}

	/**
	 * Sets the unstructured field configuration.
	 *
	 * @param fieldConfig the unstructured field configuration
	 */
	/* package */ void setFieldConfig(String fieldConfig) {
		this.fieldConfig = fieldConfig;
	}

	/**
	 * Gets the structured field configuration.
	 * @return the structured field configuration
	 */
	public Set<ModelField> getStructuredFieldConfig() {
		if (fieldConfig == null) {
			return Collections.emptySet();
		}
		return Stream.of(fieldConfig.split(FIELD_DELIMITOR)).map(ModelField::valueOf).collect(Collectors.toSet());
	}

	/**
	 * Sets the structured field configuration.
	 *
	 * @param config the structured field configuration
	 */
	public void setStructuredFieldConfig(Set<ModelField> config) {
		if (config == null || config.isEmpty()) {
			fieldConfig = null;
		} else {
			fieldConfig = config.stream().map(ModelField::name).collect(Collectors.joining(FIELD_DELIMITOR));
		}
	}

	/**
	 * Gets the description for this book type.
	 *
	 * @return the description for this book type
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description for this book type.
	 *
	 * @param description the new description for this book type
	 */
	public void setDescription(String description) {
		this.description = description;
	}
}
