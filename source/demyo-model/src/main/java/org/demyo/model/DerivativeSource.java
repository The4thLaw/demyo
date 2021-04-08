package org.demyo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Email;

import org.demyo.model.util.DefaultOrder;

/**
 * Represents the source of a Derivative.
 */
@Entity
@Table(name = "SOURCES")
@DefaultOrder(expression = @DefaultOrder.Order(property = "name"))
public class DerivativeSource extends AbstractBasicLegalEntity {
	/** The owner name. */
	@Column(name = "owner")
	private String owner;

	/** The contact email. */
	@Column(name = "email")
	@Email
	private String email;

	/** The physical address. */
	@Column(name = "address")
	private String address;

	/** The phone number. */
	@Column(name = "phone_number")
	private String phoneNumber;

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
}
