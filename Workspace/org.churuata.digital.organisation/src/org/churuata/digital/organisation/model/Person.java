package org.churuata.digital.organisation.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.condast.commons.na.model.IName;
import org.condast.commons.na.model.IProfessional;
import org.condast.commons.na.model.IContact;
import org.condast.commons.na.model.IContact.ContactTypes;
import org.condast.commons.na.model.IContactPerson;

/**
 * The persistent class for the eet_tb_persoon database table.
 */
@Entity(name="PERSON")
public class Person implements IContactPerson, Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="PERSON_ID", nullable=false)
	@PrimaryKeyJoinColumn(name="PERSON_ID")
	private long id;
	
	private long userId;
	
	private String name;
	private String title;
	private String description;

	@ElementCollection(fetch = FetchType.EAGER)
	private Collection<Contact> contacts;

	@Column( nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;

	public Person( ){
		this.contacts = new ArrayList<Contact>();
		this.createDate = Calendar.getInstance().getTime();
	}

	public Person( long userId, String name, String title, String description, IContact contact ){
		this();
		this.userId = userId;
		this.name = name;
		this.title = title;
		this.description = description;
		this.contacts.add((Contact) contact);
		this.createDate = Calendar.getInstance().getTime();
	}

	@Override
	public long getContactId() {
		return this.id;
	}

	public void setPersonId( long personId) {
		this.id = personId;
	}

	@Override
	public long getUserId() {
		return userId;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	
	
	public Date getCreateDate() {
		return createDate;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		Person p;
		
		try {
			p = this.getClass().newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Cannot clone " + IProfessional.class.getName());
		}
		
		p.setName( this.name );
		p.setTitle( this.title);
		p.setDescription(this.description);
		return p;
	}
	

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append( name );
		buffer.append( "\n");
		//if a person is a mainmember: hobbies are qualities according to the design. So here the hobbies should be be null. 
		//If a person is not a mainmember here the hobbies can have a value.
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

	@Override
	public IContact[] getContactTypes() {
		return this.contacts.toArray( new Contact[ this.contacts.size()]);
	}

	@Override
	public void clearContacts() {
		this.contacts.clear();
	}

	@Override
	public void addContact(ContactTypes type, String value) {
		Contact contact = new Contact( type, value );
		this.contacts.add( contact );
	}

	@Override
	public void addContact( IContact contact) {
		this.contacts.add(( Contact )contact );
	}

	@Override
	public void removeContact( IContact contact) {
		this.contacts.remove( contact );
	}

	@Override
	public void setContacts(IContact[] contacts) {
		this.contacts.clear();
		for( IContact contact: contacts )
			addContact( contact );
	}
}