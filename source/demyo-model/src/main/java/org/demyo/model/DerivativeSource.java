package org.demyo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.demyo.model.util.DefaultOrder;
import org.demyo.model.util.StartsWithField;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

/**
 * Represents the source of a Derivative.
 */
@Entity
@Table(name = "SOURCES")
@DefaultOrder(expression = @DefaultOrder.Order(property = "name"))
public class DerivativeSource extends AbstractModel {
	/** The name of the Source. */
	@Column(name = "name")
	@NotBlank
	@StartsWithField
	private String name;

	@Override
	public String getIdentifyingName() {
		return getName();
	}

	/** The owner name. */
	@Column(name = "owner")
	private String owner;

	/** The contact email. */
	@Column(name = "email")
	@Email
	private String email;

	/** The source website. */
	@Column(name = "website")
	@URL
	private String website;

	/** The physical address. */
	@Column(name = "address")
	private String address;

	/** The phone number. */
	@Column(name = "phone_number")
	private String phoneNumber;

	/** The history of this Source. */
	@Column(name = "history")
	private String history;

	/**
	 * Gets the name of the Source.
	 * 
	 * @return the name of the Source
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the Source.
	 * 
	 * @param name the new name of the Source
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the owner name.
	 * 
	 * @return the owner name
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * Sets the owner name.
	 * 
	 * @param owner the new owner name
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}

	/**
	 * Gets the contact email.
	 * 
	 * @return the contact email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Sets the contact email.
	 * 
	 * @param email the new contact email
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Gets the source website.
	 * 
	 * @return the source website
	 */
	public String getWebsite() {
		return website;
	}

	/**
	 * Sets the source website.
	 * 
	 * @param website the new source website
	 */
	public void setWebsite(String website) {
		this.website = website;
	}

	/**
	 * Gets the physical address.
	 * 
	 * @return the physical address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * Sets the physical address.
	 * 
	 * @param address the new physical address
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * Gets the phone number.
	 * 
	 * @return the phone number
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * Sets the phone number.
	 * 
	 * @param phoneNumber the new phone number
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	/**
	 * Gets the history of this Source.
	 * 
	 * @return the history of this Source
	 */
	public String getHistory() {
		return history;
	}

	/**
	 * Sets the history of this Source.
	 * 
	 * @param history the new history of this Source
	 */
	public void setHistory(String history) {
		this.history = history;
	}
}
