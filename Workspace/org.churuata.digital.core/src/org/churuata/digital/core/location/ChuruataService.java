package org.churuata.digital.core.location;

import java.util.Calendar;
import java.util.Date;

import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.na.model.IAddress;

@Deprecated
public class ChuruataService implements IChuruataService, Comparable<IChuruataService>{

	private Services type;
	
	private String description;
	
	private Contribution contribution;
	
	private long fromDate;
	private long toDate;
	
	
	public ChuruataService() {
		super();
	}

	public ChuruataService(Services type) {
		this( type, Contribution.LOG );
	}

	public ChuruataService(Services type, String description) {
		this( type, description, Contribution.LOG);
	}

	public ChuruataService(Services type, Contribution contribution) {
		this( type, null, contribution);
	}
	
	public ChuruataService(Services type, String description, Contribution contribution) {
		super();
		this.type = type;
		this.description = description;
		this.contribution = contribution;
		Calendar current = Calendar.getInstance();
		fromDate = current.getTimeInMillis();
		current.add( Calendar.DAY_OF_YEAR, 60);
		toDate = current.getTimeInMillis();
	}

	@Override
	public long getId() {
		// TODO Auto-generated method stub
		return 0;
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
	public Services getService() {
		return type;
	}
	
	@Override
	public Contribution getContribution() {
		return contribution;
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
		calendar.setTimeInMillis(toDate);
		return calendar.getTime();
	}

	@Override
	public void setTo(Date date) {
		this.toDate = date.getTime();
	}

	@Override
	public int compareTo(IChuruataService o) {
		return type.toString().compareTo(o.getService().toString());
	}

	@Override
	public void setService(Services type) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setContribution(Contribution contribution) {
		this.contribution = contribution;
	}

	@Override
	public LatLng getLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLocation(LatLng location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IAddress getAddress() {
		// TODO Auto-generated method stub
		return null;
	}
}
