package org.churuata.digital.core.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import org.condast.commons.na.model.IName;
import org.condast.commons.na.model.IProfessional;
import org.condast.commons.na.model.Gender;
import org.condast.commons.na.model.IContact;
import org.condast.commons.na.model.IContactPerson;

/**
 * The persistent class for the eet_tb_persoon database table.
 */
public class PersonData implements IName, Serializable {
	private static final long serialVersionUID = 1L;

	private long personId;
	
	private long userId;
	
	private String name;
	private String title;
	private String description;

	private String firstName;

	private String surname;

	private String prefix;
	
	private Date birthDate;
	
	private Gender gender;

	private Collection<ContactData> contacts;

	private long createDate;

	public PersonData( ){
		this.contacts = new ArrayList<ContactData>();
		this.createDate = Calendar.getInstance().getTime().getTime();
	}

	public PersonData( IContactPerson person ){
		this();
		this.personId = person.getId();
		this.userId = person.getUserId();
		this.name = person.getName();
		this.title = person.getTitle();
		this.firstName = person.getFirstName();
		this.prefix = person.getPrefix();
		this.surname = person.getSurname();
		this.description = person.getDescription();
		for( IContact ct: person.getContactTypes()) {
			this.contacts.add( new ContactData( ct));
		}
		this.createDate = person.getCreateDate().getTime();
	}

	public long getPersonId() {
		return personId;
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

	
	@Override
	public String getFirstName() {
		return this.firstName;
	}

	@Override
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Override
	public void setCallingName(String callingName) {
		this.name = callingName;
	}

	@Override
	public String getPrefix() {
		return this.prefix;
	}

	@Override
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	@Override
	public String getSurname() {
		return this.surname;
	}

	@Override
	public void setSurname(String surName) {
		this.surname = surName;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public long getCreateDate() {
		return createDate;
	}

	public void setCreateDate(long createDate) {
		this.createDate = createDate;
	}

	@Override
	public int compareTo(IName o) {
		// TODO Auto-generated method stub
		return 0;
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