package org.churuata.digital.organisation.model;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.churuata.digital.core.location.IChuruataType;
import org.churuata.digital.core.model.IOrganisation;
import org.condast.commons.Utils;

@Entity
public class Service implements IChuruataType {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	private String serviceType;
	private String value;
	
	private String description;
	
	private String contributor;

	@ManyToOne(cascade=CascadeType.PERSIST, fetch=FetchType.LAZY)
	private Organisation organisation;

	private long fromDate;
	
	private long toDate;
	
	public Service() {
		this( -1, IChuruataType.Types.UNKNOWN, null );
	}

	public Service( long id, IChuruataType.Types type, String value ){
		this.id = id;
		this.serviceType = type.name();
		this.value = value;
		Calendar calendar = Calendar.getInstance();
		this.fromDate = calendar.getTimeInMillis();
		calendar.add(Calendar.YEAR, 1);
		this.toDate = calendar.getTimeInMillis();
	}	
	
	@Override
	public long getId() {
		return id;
	}

	@Override
	public IOrganisation getOrganisation() {
		return organisation;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public IChuruataType.Types getType() {
		if( Utils.assertNull( this.serviceType ))
			return IChuruataType.Types.UNKNOWN;
		return IChuruataType.Types.valueOf(this.serviceType);
	}

	public void setType( IChuruataType.Types type) {
		this.serviceType = type.name();
	}

	@Override
	public String getContributor() {
		return this.contributor;
	}

	@Override
	public Contribution getContribution() {
		return Contribution.valueOf(contributor);
	}

	@Override
	public Date from() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(fromDate);
		return calendar.getTime();
	}

	@Override
	public void setFrom(Date date) {
		this.fromDate = date.getTime();
	}

	@Override
	public Date to() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis( toDate);
		return calendar.getTime();
	}

	@Override
	public void setTo(Date date) {
		this.toDate = date.getTime();
	}

	@Override
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append( this.serviceType );
		buffer.append(": ");
		buffer.append( this.value );
		return buffer.toString();
	}

}
