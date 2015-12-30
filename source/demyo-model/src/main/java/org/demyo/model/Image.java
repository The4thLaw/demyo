package org.demyo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.demyo.model.util.DefaultOrder;
import org.demyo.utils.io.DIOUtils;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Represents an image.
 */
@Entity
@Table(name = "IMAGES")
@DefaultOrder(expression = @DefaultOrder.Order(property = "description"))
public class Image extends AbstractModel {
	/** The URL to access the image. */
	@Column(name = "url")
	@NotBlank
	private String url;
	/** The description of the image. */
	@Column(name = "description")
	private String description;

	@Override
	public String getIdentifyingName() {
		return getDescription();
	}

	/**
	 * Gets a file name that makes sense to the user, regardless of the name on disk.
	 * 
	 * @return a file name.
	 */
	public String getUserFileName() {
		String fileExtension = DIOUtils.getFileExtension(url);
		if (fileExtension == null) {
			// Reasonable default, more useful to most people than a ".dat" or something
			fileExtension = "jpg";
		}
		String basename = getIdentifyingName();
		return basename + "." + fileExtension;
	}

	/**
	 * Gets the URL to access the image.
	 * 
	 * @return the URL to access the image
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Sets the URL to access the image.
	 * 
	 * @param url the new URL to access the image
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Gets the description of the image.
	 * 
	 * @return the description of the image
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description of the image.
	 * 
	 * @param description the new description of the image
	 */
	public void setDescription(String description) {
		this.description = description;
	}
}
