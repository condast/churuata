package org.churuata.digital.core.data;

import java.io.Serializable;

import org.condast.commons.Utils;
import org.condast.commons.na.model.IContact;
import org.condast.commons.na.model.IContact.ContactTypes;

public class ContactData implements Serializable {
	private static final long serialVersionUID = -7296625942864747857L;
	
	private String contactType;
	private String value;
	private boolean restricted; //restricted to this application only
	private int application;
	
	public ContactData() {
		this( ContactTypes.UNKNOWN, null );
	}

	public ContactData( IContact contact ){
		this.contactType = contact.getContactType().name();
		this.value = contact.getValue();
		this.application = contact.getApplication();
		this.restricted = contact.isRestricted();
	}

	public ContactData( ContactTypes type, String value ){
		this.contactType = type.name();
		this.value = value;
		this.application = -1;
	}
	
	public ContactTypes getContactType() {
		if( Utils.assertNull( this.contactType ))
			return ContactTypes.UNKNOWN;
		return ContactTypes.valueOf(this.contactType);
	}

	public String getValue() {
		return this.value;
	}

	public boolean isRestricted() {
		return restricted;
	}

	public int getApplication() {
		return application;
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
