package org.churuata.digital.core.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import org.condast.commons.na.model.IName;
import org.condast.commons.na.model.IProfessional;
import org.condast.commons.na.model.IContact;
import org.condast.commons.na.model.IContactPerson;

/**
 * The persistent class for the eet_tb_persoon database table.
 */
public class PersonData implements Serializable {
	private static final long serialVersionUID = 1L;

	private long personId;
	
	private long userId;
	
	private String name;
	private String title;
	private String description;

	private Collection<ContactData> contacts;

	private long createDate;

	public PersonData( ){
		this.contacts = new ArrayList<ContactData>();
		this.createDate = Calendar.getInstance().getTime().getTime();
	}

	public PersonData( IContactPerson person ){
		this();
		this.userId = person.getUserId();
		this.name = person.getName();
		this.title = person.getTitle();
		this.description = person.getDescription();
		for( IContact ct: person.getContactTypes()) {
			this.contacts.add( new ContactData( ct));
		}
		this.createDate = person.getCreateDate().getTime();
	}

	public long getContactId() {
		return this.personId;
	}

	public long getUserId() {
		return userId;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getTitle() {
		return title;
	}

	public long getCreateDate() {
		return createDate;
	}

	public void setCreateDate(long createDate) {
		this.createDate = createDate;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append( name );
		buffer.append( "\n");
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