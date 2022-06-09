package org.churuata.digital.organisation.model;

import javax.persistence.Embeddable;

import org.churuata.digital.core.model.IChuruataService;
import org.condast.commons.Utils;

@Embeddable
public class Service implements IChuruataService {

	private long id;
	
	private String serviceType;
	private String value;
	
	private String description;
	
	public Service() {
		this( -1, ServiceTypes.UNKNOWN, null );
	}

	public Service( long id, ServiceTypes type, String value ){
		this.id = id;
		this.serviceType = type.name();
		this.value = value;
	}
	
	@Override
	public long getServiceId() {
		return id;
	}

	@Override
	public ServiceTypes getServiceType() {
		if( Utils.assertNull( this.serviceType ))
			return ServiceTypes.UNKNOWN;
		return ServiceTypes.valueOf(this.serviceType);
	}

	@Override
	public void setServiceType( ServiceTypes contactType) {
		this.serviceType = contactType.name();
	}

	public void setServiceType(String contactType) {
		this.serviceType = contactType;
	}

	@Override
	public String getValue() {
		return this.value;
	}

	@Override
	public void setValue(String value) {
		this.value = value;
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
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append( this.serviceType );
		buffer.append(": ");
		buffer.append( this.value );
		return buffer.toString();
	}

}
