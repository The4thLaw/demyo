package org.demyo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.Table;

import org.demyo.model.util.DefaultOrder;

/**
 * Represents a Collection of comics within a {@link Publisher}.
 */
@Entity
@Table(name = "COLLECTIONS")
@DefaultOrder(expression =
{ @DefaultOrder.Order(property = "name") })
@NamedEntityGraph(name = "Collection.forEdition", attributeNodes =
{ @NamedAttributeNode(value = "logo"),
		@NamedAttributeNode(value = "publisher") })
public class Collection extends AbstractLegalEntity {
	/** The parent Publisher of the Collection. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "publisher_id")
	private Publisher publisher;

	/**
	 * Gets the parent Publisher of the Collection.
	 *
	 * @return the parent Publisher of the Collection
	 */
	public Publisher getPublisher() {
		return publisher;
	}

	/**
	 * Sets the parent Publisher of the Collection.
	 *
	 * @param publisher the new parent Publisher of the Collection
	 */
	public void setPublisher(Publisher publisher) {
		this.publisher = publisher;
	}
}
