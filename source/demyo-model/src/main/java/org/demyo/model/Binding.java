package org.demyo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import org.demyo.model.util.DefaultOrder;

/**
 * Represents a type of binding.
 */
@Entity
@Table(name = "BINDINGS")
@DefaultOrder(expression = @DefaultOrder.Order(property = "name"))
public class Binding extends AbstractNamedModel {
}
