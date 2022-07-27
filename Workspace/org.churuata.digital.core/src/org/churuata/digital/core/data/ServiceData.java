package org.churuata.digital.core.data;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import org.churuata.digital.core.location.IChuruataService;
import org.condast.commons.Utils;
import org.condast.commons.strings.StringStyler;
import org.condast.commons.strings.StringUtils;

public class ServiceData implements IChuruataService {

	public enum Parameters{
		PERSON_ID,
		ORGANISATION_ID,
		TYPE,
		NAME,
		DESCRIPTION,
		FROM_DATE,
		TO_DATE, LATITUDE, LONGITUDE;

		@Override
		public String toString() {
			return StringStyler.xmlStyleString( name());
		}
	}

	private long id;
	
	private String serviceType;
	
	private String description;
	
	private String contribution;
	
	private long fromDate;
	
	private long toDate;
	
	public ServiceData() {
		this( -1, IChuruataService.Services.UNKNOWN );
	}

	public ServiceData( long id, IChuruataService.Services type ){
		this.id = id;
		this.serviceType = type.name();
		this.contribution = Contribution.LOG.name();
		Calendar calendar = Calendar.getInstance();
		this.fromDate = calendar.getTimeInMillis();
		calendar.add(Calendar.YEAR, 1);
		this.toDate = calendar.getTimeInMillis();
	}	
	
	public ServiceData(IChuruataService service) {
		this.id = service.getId();
		this.serviceType = service.getService().name();
		this.description = service.getDescription();
		IChuruataService.Contribution contribution = service.getContribution(); 
		this.contribution = ( contribution == null )?Contribution.LOG.name(): contribution.name();
		this.fromDate = service.from().getTime();
		this.toDate = service.to().getTime();
	}

	@Override
	public long getId() {
		return id;
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
		if( StringUtils.isEmpty( this.serviceType ))
			return IChuruataService.Services.UNKNOWN;
		return IChuruataService.Services.valueOf(this.serviceType);
	}

	public void setService( IChuruataService.Services type) {
		this.serviceType = type.name();
	}

	@Override
	public Contribution getContribution() {
		return Contribution.valueOf(contribution);
	}
	
	@Override
	public void setContribution(Contribution contribution) {
		this.contribution = contribution.name();
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
		buffer.append( this.contribution );
		return buffer.toString();
	}

	public static long[] getIds( Collection<IChuruataService> batch ) {
		int size = Utils.assertNull(batch)?0:batch.size();
		long[] results = new long[size];
		if( size == 0)
			return results;
		int i=0;
		for( IChuruataService data: batch) 
			results[i++] = data.getId();
		return results;
	}
}
