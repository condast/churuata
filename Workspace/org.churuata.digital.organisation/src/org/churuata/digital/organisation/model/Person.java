package org.churuata.digital.organisation.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.condast.commons.na.model.IName;
import org.condast.commons.na.model.IProfessional;
import org.condast.commons.Utils;
import org.condast.commons.na.model.IContact;
import org.condast.commons.na.model.IContact.ContactTypes;
import org.condast.commons.settings.ISettingsSupport;
import org.condast.commons.na.model.IContactPerson;

/**
 * The persistent class for the eet_tb_persoon database table.
 */
@Entity(name="PERSON")
public class Person implements IContactPerson, ISettingsSupport,Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="PERSON_ID", nullable=false)
	@PrimaryKeyJoinColumn(name="PERSON_ID")
	private long id;
	
	private long userId;
	
	private String name;
	private String prefix;
	private String surName;
	
	private boolean verified;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	private Collection<Contact> contacts;
	
	@ElementCollection
	@CollectionTable(name = "settings",
	joinColumns = { @JoinColumn(name = "settings_id") })
	@MapKeyColumn(name = "item")
	@Column(name = "quantity")
	private Map<String, String> settings;


	@Column( nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;

	public Person( ){
		this.verified = false;
		this.contacts = new ArrayList<Contact>();
		this.createDate = Calendar.getInstance().getTime();
	}

	public Person( long userId, String name, String surname, String prefix, IContact contact ){
		this();
		this.userId = userId;
		this.name = name;
		this.prefix = prefix;
		this.surName = surname;
		this.contacts.add((Contact) contact);
	}

	@Override
	public long getId() {
		return this.id;
	}

	public long getUserId() {
		return userId;
	}

	public void setPersonId( long personId) {
		this.id = personId;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.surName = description;
	}

	public void setTitle(String title) {
		this.prefix = title;
	}
	
	public boolean isVerified() {
		return verified;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
	}

	@Override
	public void putSetting( String key, String value ) {
		settings.put(key, value);
	}

	@Override
	public String getSetting( String key ) {
		return settings.get(key);
	}

	@Override
	public Map<String, String> getSettings(){
		return settings;
	}
	
	@Override
	public long getCreateDate() {
		return this.createDate.getTime();
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
		p.setTitle( this.prefix);
		p.setDescription(this.surName);
		p.setVerified(this.verified);
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
	public void setContacts(IContact[] contacts) {
		this.contacts.clear();
		for( IContact contact: contacts )
			addContact( contact );
	}

	@Override
	public void removeContact( IContact contact) {
		this.contacts.remove( contact );
	}

	public boolean removeContact(long id) {
		if( Utils.assertNull(contacts))
			return false;
		Collection<IContact> temp = new ArrayList<>( this.contacts);
		temp.forEach( c->{ 
			if( c.getId() == id )
				removeContact(c);
		});
		return false;
	}

	@Override
	public String getFirstName() {
		return this.name;
	}

	@Override
	public void setFirstName(String firstName) {
		this.name = firstName;
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
		return this.surName;
	}

	@Override
	public void setSurname(String surName) {
		this.surName = surName;
	}

	@Override
	public int compareTo(IName o) {
		int compare = this.name.compareTo(o.getFirstName());
		if( compare != 0 )
			return compare;
		compare = this.surName.compareTo(o.getSurname());
		if( compare != 0 )
			return compare;
		compare = this.prefix.compareTo(o.getPrefix());
		return compare;
	}
}