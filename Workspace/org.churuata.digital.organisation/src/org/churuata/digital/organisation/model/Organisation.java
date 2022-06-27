package org.churuata.digital.organisation.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import javax.persistence.Basic;
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
	
	private double latitude;
	private double longitude;
	
	@Basic(optional = true)
	@Column( nullable=true)
	private Address address;

	@JoinColumn( nullable=false)
	@OneToOne
	private Person contact;
	
	private String name;
	
	private String description;
	
	private String website;

	@OneToMany( mappedBy="organisation", cascade = CascadeType.ALL, orphanRemoval = true)
	private Collection<Service> services;

	@Column( nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;

	public Organisation( ){
		this.createDate = Calendar.getInstance().getTime();
	}

	public Organisation( String name, String description ){
		this.name = name;
		this.description = description;
		this.createDate = Calendar.getInstance().getTime();
	}

	public Organisation( IContactPerson contact, String name, String description ){
		this();
		this.contact= (Person) contact;
		this.name = name;
		this.description = description;
		this.services = new ArrayList<Service>();
		this.createDate = Calendar.getInstance().getTime();
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
		return new LatLng( this.name, this.description, latitude, longitude );
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String title) {
		this.name = title;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public IContactPerson getContact() {
		return contact;
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
	public IAddress getAddress() {
		return address;
	}

	public void addAddress( IAddress address) {
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
	public void addService( IChuruataService type, String value) {
		Service contact = new Service( this.services.size(), type.getService(), value );
		this.services.add( contact );
	}

	@Override
	public int addService( IChuruataService contact) {
		this.services.add(( Service )contact );
		return this.services.size()-1;
	}

	@Override
	public void removeService( IChuruataService contact) {
		this.services.remove( contact );
	}

	@Override
	public void removeService( long serviceId) {
		Collection<IChuruataService> temp = new ArrayList<>( this.services );
		temp.forEach( s-> {
			if( s.getId() == serviceId )
				this.services.remove(s);
		});
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
		String str = ( name + ": ");
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