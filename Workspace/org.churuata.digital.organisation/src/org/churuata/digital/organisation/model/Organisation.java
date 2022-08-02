package org.churuata.digital.organisation.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.churuata.digital.core.data.ChuruataOrganisationData;
import org.churuata.digital.core.location.IChuruataService;
import org.churuata.digital.core.model.IOrganisation;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.na.model.IAddress;
import org.condast.commons.na.model.IContactPerson;
import org.condast.commons.na.model.IName;
import org.condast.commons.na.model.IProfessional;

/**
 * The persistent class
 */
@Entity(name="ORGANISATION")
public class Organisation implements IOrganisation, Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	String name;
	
	String description;
	
	@JoinColumn( nullable=true)
	@OneToOne
	private Location location;
	
	@JoinColumn( nullable=true)
	@OneToOne
	private Address address;

	@JoinColumn( nullable=false)
	@OneToOne
	private Person contact;
		
	private String website;
	
	private int type;
	
	private boolean verified;
	
	private int score;
	
	private boolean principal;

	@OneToMany(mappedBy="organisation", cascade=CascadeType.ALL, orphanRemoval = true)
	private Collection<Service> services;

	@Column( nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;

	@Column( nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateDate;

	public Organisation( ){
		this.createDate = Calendar.getInstance().getTime();
		this.updateDate = Calendar.getInstance().getTime();
	}

	public Organisation( Location location, boolean principal ){
		this();
		this.name = location.getName();
		this.description = location.getDescription();
		this.verified = false;
		this.score = 0;
		this.type = OrganisationTypes.UNKNOWN.getIndex();
		this.principal = principal;
	}

	public Organisation( IContactPerson contact, ChuruataOrganisationData data ){
		this( data );
		this.contact = (Person) contact;
	}
	
	public Organisation( ChuruataOrganisationData data ){
		this();
		this.name = data.getName();
		this.description = data.getDescription();
		this.website = data.getWebsite();
		this.services = new ArrayList<Service>();
		this.type = data.getType().getIndex();
		this.verified = data.isVerified();
		this.score = data.getScore();
		this.createDate = Calendar.getInstance().getTime();
		this.updateDate = Calendar.getInstance().getTime();
	}

	@Override
	public long getId() {
		return this.id;
	}

	public void setId( long organisationId) {
		this.id = organisationId;
	}

	@Override
	public LatLng getLocation() {
		return ( location == null )?null: location.getLocation();
	}

	public void setLocation( double latitude, double longitude ) {
		this.location.setLatitude(latitude);
		this.location.setLongitude(longitude);
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	@Override
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name  = name;
	}

	@Override
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	@Override
	public IContactPerson getContact() {
		return contact;
	}
	
	@Override
	public OrganisationTypes getType() {
		return OrganisationTypes.values()[ type ];
	}

	public void setType(OrganisationTypes type) {
		this.type = type.getIndex();
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

	public boolean isPrincipal() {
		return principal;
	}

	public void setPrimary(boolean primary) {
		this.principal = primary;
	}

	@Override
	public IAddress getAddress() {
		return address;
	}

	public void setAddress( IAddress address) {
		this.address = (Address) address;
	}

	@Override
	public IChuruataService[] getServices() {
		return this.services.toArray( new Service[ this.services.size()]);
	}

	@Override
	public void clearServices() {
		this.services.clear();
	}

	@Override
	public int addService( IChuruataService cservice) {
		Service service = (Service) cservice;
		service.setOrganisation(this);
		this.services.add(service );
		return this.services.size()-1;
	}

	@Override
	public boolean removeService( IChuruataService contact) {
		return this.services.remove( contact );
	}

	@Override
	public boolean removeService( long serviceId) {
		boolean result = false;
		Collection<IChuruataService> temp = new ArrayList<>( this.services );
		for( IChuruataService s: temp ){
			if( s.getId() == serviceId ) {
				this.services.remove(s);
				result = true;
			}
		}
		return result;
	}

	@Override
	public void removeService(String type, String value) {
		Collection<IChuruataService> temp = new ArrayList<>( this.services );
		temp.forEach( s-> {
			if( s.getService().name().equals(type) && s.getDescription().equals(value))
				this.services.remove(s);
		});
	}

	@Override
	public void setServices(IChuruataService[] services) {
		this.services.clear();
		for( IChuruataService contact: services )
			addService( contact );
	}

	@Override
	public int getServicesSize() {
		return this.services.size();
	}
	
	public Date getCreateDate() {
		return createDate;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		Organisation p;
		
		try {
			p = this.getClass().newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Cannot clone " + IProfessional.class.getName());
		}	
		return p;
	}
	

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		String str = ( this.location.getName() + ": ");
		buffer.append( str );
		buffer.append( "\n");
		buffer.append( address );
		buffer.append( str );
		return buffer.toString();
	}

	/**
	 * @return
	 */
	public static final String getFullName( IProfessional table ) {
		String retval = "";
		IName naam = table.getName();
		if (naam != null) {
			retval = naam.getName();
		}
		return retval;
	}
}