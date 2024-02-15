package org.demyo.model;

import java.util.SortedSet;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.SortComparator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.demyo.model.jackson.SortedSetDeserializer;
import org.demyo.model.util.DefaultOrder;
import org.demyo.model.util.IdentifyingNameComparator;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Represents a Publisher of comics.
 */
@Entity
@Table(name = "PUBLISHERS")
@DefaultOrder(expression =
{ @DefaultOrder.Order(property = "name") })
@NamedEntityGraph(name = "Publisher.forIndex", attributeNodes = @NamedAttributeNode("collections"))
@NamedEntityGraph(name = "Publisher.forView", attributeNodes =
{ @NamedAttributeNode("collections"),
		@NamedAttributeNode("logo") })
@NamedEntityGraph(name = "Publisher.forEdition", attributeNodes = @NamedAttributeNode("logo"))
public class Publisher extends AbstractLegalEntity {
	/** The collections belonging to this publisher. */
	@OneToMany(mappedBy = "publisher", fetch = FetchType.LAZY)
	@BatchSize(size = BATCH_SIZE)
	@SortComparator(IdentifyingNameComparator.class)
	@JsonIgnoreProperties("publisher")
	@JsonView(ModelView.Basic.class)
	@JsonDeserialize(using = SortedSetDeserializer.class)
	private SortedSet<Collection> collections;

	/**
	 * Gets the collections belonging to this publisher.
	 *
	 * @return the collections belonging to this publisher
	 */
	public SortedSet<Collection> getCollections() {
		return collections;
	}

}
