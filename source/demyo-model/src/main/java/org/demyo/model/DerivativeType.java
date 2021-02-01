package org.demyo.model;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.demyo.model.util.DefaultOrder;

/**
 * Represents a type of Derivative.
 */
@Entity
@Table(name = "DERIVATIVE_TYPES")
@DefaultOrder(expression = @DefaultOrder.Order(property = "name"))
public class DerivativeType extends AbstractNamedModel {
}
