/**
 * 
 */
package org.churuata.digital.authentication.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.condast.commons.Utils;
import org.condast.commons.na.model.IName;

/**
 * @author roel
 *
 */
@Embeddable
public class Name implements Comparable<IName>, IName {
	
	@Column(nullable=false)
	private String firstName;

	@Column(name = "CALLING_NAME")
	private String callingName;

	@Column(nullable=false)
	private String surname;

	@Column
	private String prefix;

	/**
	 * 
	 */
	public Name() {
		this.firstName = "";
		this.prefix = "";
		this.surname = "";
	}

	/**
	 * @param voornaam
	 * @param prefix
	 * @param SurName
	 */
	public Name(String voornaam, String prefix, String SurName) {
		this.firstName = voornaam;
		this.prefix = prefix;
		this.surname = SurName;
	}

	/* (non-Javadoc)
	 * @see nl.cultuurinzicht.eetmee.domein.impl.INaam#getVoornaam()
	 */
	@Override
	public final String getFirstName() {
		return firstName;
	}
	/* (non-Javadoc)
	 * @see nl.cultuurinzicht.eetmee.domein.impl.INaam#setVoornaam(java.lang.String)
	 */
	@Override
	public final void setFirstName(String voornaam) {
		this.firstName = voornaam;
	}

	@Override
	public String getName() {
		return this.callingName;
	}

	
	@Override
	public void setName(String name) {
		this.callingName = name;
	}

	@Override
	public void setCallingName(String roepnaam) {
		this.callingName = roepnaam;
	}

	/* (non-Javadoc)
	 * @see nl.cultuurinzicht.eetmee.domein.impl.INaam#getprefix()
	 */
	@Override
	public final String getPrefix() {
		return prefix;
	}

	/* (non-Javadoc)
	 * @see nl.cultuurinzicht.eetmee.domein.impl.INaam#setprefix(java.lang.String)
	 */
	@Override
	public final void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	/* (non-Javadoc)
	 * @see nl.cultuurinzicht.eetmee.domein.impl.INaam#getSurName()
	 */
	@Override
	public final String getSurname() {
		return surname;
	}
	/* (non-Javadoc)
	 * @see nl.cultuurinzicht.eetmee.domein.impl.INaam#setSurName(java.lang.String)
	 */
	@Override
	public final void setSurname(String surname) {
		this.surname = surname;
	}

	/* (non-Javadoc)
	 * @see nl.cultuurinzicht.eetmee.domein.impl.INaam#getName()
	 */
	@Override
	public String toString() {
		StringBuffer s = new StringBuffer();
		if (!Utils.assertNull( firstName)) {
			s.append(firstName.trim());
		}
		if (!Utils.assertNull( callingName)) {
			s.append( " (" + callingName.trim() + ")");
		}
		if (!Utils.assertNull(prefix)) {
			s.append(" ");
			s.append(prefix.trim());
		}
		if (!Utils.assertNull(surname)) {
			s.append(" ");
			s.append(surname.trim());
		}
		return s.toString();
	}
	

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	/* (non-Javadoc)
	 * @see nl.cultuurinzicht.eetmee.domein.impl.INaam#compareTo(nl.cultuurinzicht.eetmee.domein.impl.Naam)
	 */
	@Override
	public int compareTo(IName o) {
		return surname.compareTo(o.getSurname());
	}
}
