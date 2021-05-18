package org.satr.arnac.authentication.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.condast.commons.Utils;
import org.condast.commons.na.model.Gender;
import org.condast.commons.na.model.IAddress;
import org.condast.commons.na.model.IName;
import org.condast.commons.na.model.IPerson;
import org.condast.commons.na.model.IProfessional;
import org.condast.commons.na.model.IPersonAddress;
import org.condast.commons.na.model.IAddress.AddressTypes;
import org.condast.commons.date.DateUtils;

/**
 * The persistent class for the eet_tb_persoon database table.
 */
@Entity(name="PERSON")
public class Person implements IPerson, Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="PERSON_ID", nullable=false)
	@PrimaryKeyJoinColumn(name="PERSON_ID")
	private long personId;
	
	@Embedded
	private Name name;

	@Column( nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date birthDate;

	@Column( nullable=false)
	private String gender;
	
	private String title;
	
	@Column( nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;

	@OneToMany(mappedBy="person", cascade = CascadeType.ALL, fetch=FetchType.EAGER)
	private Map<String, PersonAddress> addresses;

	@Column( nullable=true)
	private String hobbies;

	public Person( ){
		this( new Name() );
	}

	public Person( IName name ){
		this.name = (Name) name;
		this.addresses = new HashMap<String,PersonAddress>();
		this.createDate = Calendar.getInstance().getTime();
	}

	@Override
	public long getPersonId() {
		return this.personId;
	}

	public void setPersonId( long persoonId) {
		this.personId = persoonId;
	}

	@Override
	public IPersonAddress getAddress(AddressTypes type) {
		return addresses.get( type.name() );	
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public Date getBirthDate() {
		return this.birthDate;
	}

	@Override
	public void setBirthDate(Date ddGeboorte) {
		this.birthDate = ddGeboorte;
	}

	@Override
	public Gender getGender() {
		return Gender.convertFrom( this.gender );
	}

	public void setGender( String gender) {
		this.gender = gender;
	}

	@Override
	public void setGender( Gender gender) {
		this.gender = gender.name();
	}

	@Override
	public final Integer getAge() {
		if (this.getBirthDate()== null) {
			return -1;
		}
		Calendar geboorteDatum = Calendar.getInstance();
		geboorteDatum.setTime( this.getBirthDate() );
		
		Calendar now = new GregorianCalendar();
		int age = now.get(Calendar.YEAR) - geboorteDatum.get(Calendar.YEAR);
	     if((geboorteDatum.get(Calendar.MONTH) > now.get(Calendar.MONTH))
	       || (geboorteDatum.get(Calendar.MONTH) == now.get(Calendar.MONTH)
	       && geboorteDatum.get(Calendar.DAY_OF_MONTH) > now.get(Calendar.DAY_OF_MONTH))) {
	        age--;
	     }
	     return age;
	}
	
	@Override
	public final IName getName() {
		return name;
	}

	@Override	
	public final void setName(IName naam) {
		this.name = (Name) naam;
	}

	@Override
	public final String getFullName() {
		String retval = "";
		IName name = this.getName();
		if (name != null) {
			retval = name.toString();
		}
		return retval;
	}
	
	@Override
	public IAddress[] getAddresses() {
		return addresses.values().toArray( new IAddress[ addresses.size()]);
	}

	@Override
	public void addAddress(AddressTypes type, IPersonAddress address) {
		this.addresses.put(type.name(), (PersonAddress) address);
	}

	@Override
	public void removeAddress(IAddress address) {
		String key = null;
		Iterator<Map.Entry<String, PersonAddress>> iterator = this.addresses.entrySet().iterator();
		while(( key == null ) && ( iterator.hasNext() )) {
			Map.Entry<String, PersonAddress> entry = iterator.next();
			if( entry.getValue().getAddress().equals(address ))
				key = entry.getKey();
		}
		if( Utils.assertNull(key))
			addresses.remove( key );
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
		
		IName name = new Name( this.name.getFirstName(), this.name.getPrefix(), this.name.getSurname());
		p.setName( name );
		p.setBirthDate(this.getBirthDate());
		p.setGender(this.getGender());
		Iterator<Map.Entry<String, PersonAddress>> iterator = this.addresses.entrySet().iterator();
		while( iterator.hasNext()) {
			Map.Entry<String, PersonAddress> entry = iterator.next();
			p.addAddress( AddressTypes.valueOf( entry.getKey()), entry.getValue() );
		}
		return p;
	}
	

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		IName naam = this.getName();
		String str = ( naam != null )? naam.toString() : "";
		buffer.append( str );
		buffer.append( "\n");
		IPersonAddress address = this.getAddress( AddressTypes.MAIN );
		buffer.append( address );
		buffer.append( "\n");
		buffer.append( DateUtils.getFormatted( this.birthDate ) + ", "); 
		buffer.append( str );
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
	public IPersonAddress[] getPersonAddresses() {
		// TODO Auto-generated method stub
		return null;
	}
}