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

import org.churuata.digital.core.location.IChuruataService;
import org.churuata.digital.core.model.IOrganisation;
import org.condast.commons.Utils;
import org.condast.commons.strings.StringUtils;

@Entity
public class Service implements IChuruataService {

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
	
	public Service() {}

	public Service( IChuruataService.Services type, String value ){
		this( -1, type, value );
	}
	
	public Service( long id, IChuruataService.Services type, String value ){
		this.id = id;
		this.serviceType = type.name();
		this.value = value;
		this.contributor = Contribution.LOG.name();
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
	public IChuruataService.Services getService() {
		if( Utils.assertNull( this.serviceType ))
			return IChuruataService.Services.UNKNOWN;
		return IChuruataService.Services.valueOf(this.serviceType);
	}

	public void setService( IChuruataService.Services service) {
		this.serviceType = service.name();
	}

	@Override
	public String getContributor() {
		return this.contributor;
	}

	@Override
	public Contribution getContribution() {
		return StringUtils.isEmpty(contributor)?Contribution.LOG: Contribution.valueOf(contributor);
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

	public void setFromDate(long fromDate) {
		this.fromDate = fromDate;
	}

	public void setToDate(long toDate) {
		this.toDate = toDate;
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
