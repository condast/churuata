package org.churuata.digital.core.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import org.churuata.digital.core.location.IChuruataType;
import org.churuata.digital.core.model.IOrganisation;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.strings.StringStyler;

/**
 * The persistent class for the eet_tb_persoon database table.
 */
public class OrganisationData implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	public static final String S_ORGANISATION_REST_PATH = "organisation/admin/";
	
	public enum Requests{
		CREATE,
		FIND,
		ADD_SERVICE,
		REMOVE_SERVICE;
	}

	public enum Parameters{
		USER_ID,
		SECURITY,
		NAME,
		DESCRIPTION,
		LATITUDE,
		LONGITUDE,
		TYPE;

		@Override
		public String toString() {
			return StringStyler.xmlStyleString( name());
		}
	}

	private long organisationId;
	
	private LatLng location;
	
	private AddressData address;

	private PersonData contact;
	
	private String name;
	
	private String description;
	
	private String website;
	
	private Collection<IChuruataType> types;
	
	public OrganisationData( LatLng location ){
		this.location = location;
		types = new ArrayList<>();
	}

	public OrganisationData( LatLng location, String name, String description ){
		this( location );
		this.name = name;
		this.description = description;
	}

	public OrganisationData( IOrganisation organisation ){
		this.contact= new PersonData( organisation.getContact());
		this.name = organisation.getName();
		this.description = organisation.getDescription();
		types = new ArrayList<>();
	}

	public long getId() {
		return this.organisationId;
	}
	
	public LatLng getLocation() {
		return location;
	}

	public void setLocation(LatLng location) {
		this.location = location;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public PersonData getContact() {
		return contact;
	}

	public AddressData getAddress() {
		return address;
	}
	
	public void addChuruataType( IChuruataType type ) {
		this.types.add(type);
	}

	public IChuruataType[] getTypes() {
		return types.toArray( new IChuruataType[ this.types.size()]);
	}
	
	
	
	
}