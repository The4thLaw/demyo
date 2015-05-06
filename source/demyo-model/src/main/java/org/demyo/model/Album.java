package org.demyo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.demyo.model.util.DefaultOrder;

// TODO: Auto-generated Javadoc
// TODO: far from complete
// TODO: order by is not complete
// TODO: when saving this entity, it should at least have a title or a Series.
/**
 * The Class Album.
 */
@Entity
@Table(name = "ALBUMS")
@DefaultOrder(expression = @DefaultOrder.Order(property = "name"))
public class Album extends AbstractModel {
	/** The title. */
	@Column(name = "title")
	private String title;

	@Override
	public String getIdentifyingName() {
		// TODO: If there is no title, this should return the Series name and number
		return getTitle();
	}

	/**
	 * Gets the title.
	 * 
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the title.
	 * 
	 * @param title the new title
	 */
	public void setTitle(String title) {
		this.title = title;
	}
}
