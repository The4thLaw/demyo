package org.demyo.model;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * This class defines the base model fields.
 * 
 * @author $Author: xr $
 * @version $Revision: 1 $
 */
@MappedSuperclass
public abstract class AbstractModel implements IModel {
	@Id
	@GeneratedValue
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
