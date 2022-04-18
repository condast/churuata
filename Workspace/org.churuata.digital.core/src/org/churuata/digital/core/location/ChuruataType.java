package org.churuata.digital.core.location;

import java.util.Calendar;
import java.util.Date;

public class ChuruataType implements Comparable<IChuruataType>, IChuruataType{

	private Types type;
	
	private String description;
	
	private Contribution contribution;
	
	private String contributor;
	
	private long fromDate;
	private long toDate;
	
	public ChuruataType(Types type) {
		this( type, Contribution.LOG );
	}

	public ChuruataType(Types type, String description) {
		this( type, description, Contribution.LOG);
	}

	public ChuruataType(Types type, Contribution contribution) {
		this( type, null, contribution);
	}
	
	public ChuruataType(Types type, String description, Contribution contribution) {
		super();
		this.type = type;
		this.description = description;
		this.contributor = "home";
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
	public Types getType() {
		return type;
	}
	
	@Override
	public Contribution getContribution() {
		return contribution;
	}
	
	@Override
	public String getContributor() {
		return contributor;
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
	public int compareTo(IChuruataType o) {
		return type.toString().compareTo(o.getType().toString());
	}
}
