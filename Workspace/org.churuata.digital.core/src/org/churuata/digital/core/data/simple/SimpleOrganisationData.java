package org.churuata.digital.core.data.simple;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import org.churuata.digital.core.data.ServiceData;
import org.churuata.digital.core.location.IChuruataService;
import org.churuata.digital.core.model.IOrganisation;
import org.churuata.digital.core.model.IOrganisation.OrganisationTypes;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.na.data.ContactPersonData;
import org.condast.commons.na.model.IContactPerson;

/**
 * The persistent class for the eet_tb_persoon database table.
 */
public class SimpleOrganisationData implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	public static final String S_ORGANISATION_REST_PATH = "organisation/admin/";
	
	private long organisationId;
	
	private double latitude;
	private double longitude;
	
	private String address;

	private ContactPersonData contact;
	
	private String name;
	
	private String description;
	
	private String website;
	
	private OrganisationTypes type;
	
	private boolean verified;
	
	private int score;
	
	private Collection<ServiceData> services;
	
	public SimpleOrganisationData() {
		super();
		Calendar calendar = Calendar.getInstance();
		calendar.add( Calendar.YEAR, 1);
		services = new ArrayList<>();
		this.verified = false;
		this.score = 0;
	}

	public SimpleOrganisationData( LatLng location ){
		this.latitude = location.getLatitude();
		this.longitude = location.getLongitude();
		this.verified = false;
		this.score = 0;
		services = new ArrayList<>();
	}

	public SimpleOrganisationData( LatLng location, String name, String description ){
		this( location );
		this.name = name;
		this.description = description;
		this.verified = false;
		this.score = 0;
	}

	public SimpleOrganisationData( IOrganisation organisation ){
		this.organisationId = organisation.getId();
		LatLng location = organisation.getLocation();
		if( location != null ) {
			this.latitude = location.getLatitude();
			this.longitude = location.getLongitude();	
		}
		this.contact= new ContactPersonData( organisation.getContact());
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
		return new LatLng( this.name, this.latitude, this.longitude );
	}

	public void setLocation(LatLng latlng) {
		this.longitude = latlng.getLongitude();
		this.latitude = latlng.getLatitude();
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getWebsite() {
		return website;
	}

	public IContactPerson getContact() {
		return (IContactPerson) contact;
	}

	public OrganisationTypes getType() {
		return type;
	}

	public String getAddress() {
		return address;
	}

	public boolean isVerified() {
		return verified;
	}

	public int getScore() {
		return score;
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

	public int getServicesSize() {
		return this.services.size();
	}
}