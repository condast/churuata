package org.churuata.digital.core.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import org.churuata.digital.core.location.IChuruataService;
import org.churuata.digital.core.model.IOrganisation;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.na.data.PersonData;
import org.condast.commons.na.model.IAddress;
import org.condast.commons.na.model.IContactPerson;
import org.condast.commons.strings.StringStyler;

/**
 * The persistent class for the eet_tb_persoon database table.
 */
public class OrganisationData implements IOrganisation, Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	public static final String S_ORGANISATION_REST_PATH = "organisation/admin/";
	
	public enum Requests{
		CREATE,
		REGISTER,
		FIND,
		FIND_IN_RANGE,
		ADD_SERVICE,
		REMOVE_SERVICE, 
		SET_LOCATION;
	}

	public enum Parameters{
		USER_ID,
		SECURITY,
		NAME,
		DESCRIPTION,
		LATITUDE,
		LONGITUDE,
		RANGE,
		TYPE, 
		WEBSITE;

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
	
	private OrganisationTypes type;
	
	private boolean verified;
	
	private int score;
	
	private Collection<ServiceData> services;
	
	public OrganisationData() {
		super();
		Calendar calendar = Calendar.getInstance();
		calendar.add( Calendar.YEAR, 1);
		services = new ArrayList<>();
		this.verified = false;
		this.score = 0;
	}

	public OrganisationData( LatLng location ){
		this.location = location;
		this.verified = false;
		this.score = 0;
		services = new ArrayList<>();
	}

	public OrganisationData( LatLng location, String name, String description ){
		this( location );
		this.name = name;
		this.description = description;
		this.verified = false;
		this.score = 0;
	}

	public OrganisationData( IOrganisation organisation ){
		this.organisationId = organisation.getId();
		this.location = organisation.getLocation();
		this.contact= new PersonData( organisation.getContact());
		this.name = organisation.getName();
		this.description = organisation.getDescription();
		this.website = organisation.getWebsite();
		this.type = organisation.getType();
		this.verified = organisation.isVerified();
		this.score = organisation.getScore();
		services = new ArrayList<>();
		for( IChuruataService service: organisation.getServices())
			services.add( new ServiceData( service ));
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

	public IContactPerson getContact() {
		return (IContactPerson) contact;
	}

	public void setContact(PersonData contact) {
		this.contact = contact;
	}

	@Override
	public OrganisationTypes getType() {
		return type;
	}

	public void setType(OrganisationTypes type) {
		this.type = type;
	}

	@Override
	public boolean isVerified() {
		return verified;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
	}

	@Override
	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public IAddress getAddress() {
		return (IAddress) address;
	}
	
	public void clearServices(){
		this.services.clear();
	}
	
	public int addService( IChuruataService type ) {
		this.services.add(new ServiceData( type ));
		return this.services.size();
	}

	public IChuruataService[] getServices() {
		return services.toArray( new IChuruataService[ this.services.size()]);
	}

	public void setChuruataServices(IChuruataService[] input) {
		this.services.clear();
		for( IChuruataService cs: input )
			this.services.add( new ServiceData( cs ));
	}

	@Override
	public void setServices(IChuruataService[] services) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeService(IChuruataService service) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeService(String type, String value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeService(long serviceId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addService(IChuruataService type, String value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getServicesSize() {
		return this.services.size();
	}
}