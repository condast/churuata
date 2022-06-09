package org.churuata.digital.organisation.model;

import javax.persistence.Embeddable;

import org.condast.commons.Utils;
import org.condast.commons.na.model.IContact;

@Embeddable
public class Contact implements IContact {

	private String contactType;
	private String value;
	private boolean restricted; //restricted to this application only
	private int application;
	
	public Contact() {
		this( ContactTypes.UNKNOWN, null );
	}

	public Contact( ContactTypes type, String value ){
		this.contactType = type.name();
		this.value = value;
		this.application = -1;
	}
	
	@Override
	public ContactTypes getContactType() {
		if( Utils.assertNull( this.contactType ))
			return ContactTypes.UNKNOWN;
		return ContactTypes.valueOf(this.contactType);
	}

	@Override
	public void setContactType( ContactTypes contactType) {
		this.contactType = contactType.name();
	}

	public void setContactType(String contactType) {
		this.contactType = contactType;
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
	public boolean isRestricted() {
		return restricted;
	}

	@Override
	public void setRestricted(boolean restricted) {
		this.restricted = restricted;
	}

	public int getApplication() {
		return application;
	}

	public void setApplication(int application) {
		this.application = application;
	}

	@Override
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append( this.contactType );
		buffer.append(": ");
		buffer.append( this.value );
		return buffer.toString();
	}

}
