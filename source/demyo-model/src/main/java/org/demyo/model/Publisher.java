package org.demyo.model;

import java.util.SortedSet;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.SortComparator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.demyo.model.jackson.SortedSetDeserializer;
import org.demyo.model.util.DefaultOrder;
import org.demyo.model.util.IdentifyingNameComparator;

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
