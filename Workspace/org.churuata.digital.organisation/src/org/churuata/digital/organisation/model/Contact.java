package org.churuata.digital.organisation.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.condast.commons.na.model.IContact;
import org.condast.commons.strings.StringUtils;

@Entity
public class Contact implements IContact, Serializable {
	private static final long serialVersionUID = -5944022062333100217L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

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
	public long getId() {
		return id;
	}

	@Override
	public ContactTypes getContactType() {
		if( StringUtils.isEmpty( this.contactType ))
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
