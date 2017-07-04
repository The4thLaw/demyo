package org.demyo.model;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * This class defines the base model fields.
 */
@MappedSuperclass
public abstract class AbstractModel implements IModel {
	/**
	 * The batch size for lazy loading of collections.
	 * 
	 * @see http://stackoverflow.com/a/27055702/109813
	 */
	protected static final int BATCH_SIZE = 15;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	// Note: cannot be final due to proxies
	@Override
	public Long getId() {
		return id;
	}

	// Note: cannot be final due to proxies
	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return getId() + " - " + getIdentifyingName();
	}
}
