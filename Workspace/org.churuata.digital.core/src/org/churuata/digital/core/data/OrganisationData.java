package org.churuata.digital.core.data;

import java.io.Serializable;

import org.churuata.digital.core.model.IOrganisation;

/**
 * The persistent class for the eet_tb_persoon database table.
 */
public class OrganisationData implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	private long organisationId;
	
	private AddressData address;

	private PersonData contact;
	
	private String name;
	
	private String description;
	
	public OrganisationData( ){
	}

	public OrganisationData( String name, String description ){
		this.name = name;
		this.description = description;
	}

	public OrganisationData( IOrganisation organisation ){
		this();
		this.contact= new PersonData( organisation.getContact());
		this.name = organisation.getName();
		this.description = organisation.getDescription();
	}

	public long getId() {
		return this.organisationId;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public PersonData getContact() {
		return contact;
	}

	public AddressData getAddress() {
		return address;
	}
}