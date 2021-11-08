package org.churuata.digital.authentication.model;

import java.io.Serializable;

import javax.persistence.Column;

public class PersonAddressId implements Serializable {
	private static final long serialVersionUID = 1204445269004046827L;

	@Column( name="ADDRESS_ID")
	private long addressId;

	@Column( name="PERSON_ID")
	private long personId;

	public PersonAddressId() {
	}

	@Override
	public int hashCode() {
		return (int)(addressId + personId);
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof PersonAddressId) {
			PersonAddressId otherId = (PersonAddressId) object;
			return (otherId.addressId == this.addressId) && (otherId.personId == this.personId);
		}
		return false;
	}
}
