package org.churuata.digital.organisation.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.churuata.digital.core.model.IChuruataService;
import org.churuata.digital.core.model.IOrganisation;
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
	@Column(name="ORGANISATION_ID", nullable=false)
	@PrimaryKeyJoinColumn(name="ORGANISATION_ID")
	private long organisationId;
	
	@Basic(optional = true)
	@Column( nullable=true)
	private Address address;

	@Basic(optional = false)
	@Column( nullable=false)
	private Person contact;
	
	private String name;
	
	private String description;

	@ElementCollection(fetch = FetchType.EAGER)
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
		return this.organisationId;
	}

	public void setId( long organisationId) {
		this.organisationId = organisationId;
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
	public IAddress getAddress() {
		return address;
	}

	public void addAddress( IAddress address) {
		this.address = (Address) address;
	}

	@Override
	public IChuruataService[] getServiceTypes() {
		return this.services.toArray( new Service[ this.services.size()]);
	}

	@Override
	public void clearServices() {
		this.services.clear();
	}

	@Override
	public void addService( IChuruataService.ServiceTypes type, String value) {
		Service contact = new Service( this.services.size(), type, value );
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
			if( s.getServiceId() == serviceId )
				this.services.remove(s);
		});
	}

	@Override
	public void removeService(String type, String value) {
		Collection<IChuruataService> temp = new ArrayList<>( this.services );
		temp.forEach( s-> {
			if( s.getServiceType().name().equals(type) && s.getValue().equals(value))
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