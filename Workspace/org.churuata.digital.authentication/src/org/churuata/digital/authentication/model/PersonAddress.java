package org.churuata.digital.authentication.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.na.model.IAddress;
import org.condast.commons.na.model.ICommunity;
import org.condast.commons.na.model.IPerson;
import org.condast.commons.na.model.IProfessional;
import org.condast.commons.na.model.IPersonAddress;

@Entity(name="PERSON_ADDRESS")
public class PersonAddress implements IPersonAddress, Serializable {
	private static final long serialVersionUID = -7261371878383033675L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="PERSON_ADDRESS_ID", nullable=false)
	private long id;

	@Column( nullable = false )
	private String number;
	private String extension;
	
	private double latitude, longtitude;
	
	@ManyToOne
	@JoinColumn(referencedColumnName="ADDRESS_ID")
	private Address address;

	@ManyToOne
	@JoinColumn(referencedColumnName="PERSON_ID")
	private Person person;

	public PersonAddress() {
		this.longtitude = -1;
		this.latitude = -1;
	}

	@Override
	public long getId() {
		return id;
	}

	protected PersonAddress( IAddress address ){
		this();
		this.address = (Address) address;
		this.address.addPersonAddress(this);
	}
	
	public PersonAddress( IAddress address, IProfessional person){
		this();
		this.address = (Address) address;
		this.person = (Person) person;
	}

	public PersonAddress( IAddress address, IProfessional person, String number, double longtitude, double latitude ){
		this( address, person, number, null, longtitude, latitude );
	}

	public PersonAddress( IAddress address, IProfessional person, String number, String extension, double longtitude, double latitude ){
		this();
		this.address = (Address) address;
		this.person = (Person) person;
		this.number = number;
		this.extension = extension;
	}

	/* (non-Javadoc)
	 * @see nl.eetmee.na.model.IPersonAddress#getAddress()
	 */
	@Override
	public IAddress getAddress() {
		return address;
	}
	
	/* (non-Javadoc)
	 * @see nl.eetmee.na.model.IPersonAddress#getPerson()
	 */
	@Override
	public IProfessional getPerson() {
		return (IProfessional) person;
	}

	@Override
	public void setPerson( IPerson person) {
		this.person = (Person) person;
	}

	/* (non-Javadoc)
	 * @see nl.eetmee.na.model.IPersonAddress#getNumber()
	 */
	@Override
	public String getNumber() {
		return number;
	}

	@Override
	public void setNumber(String number) {
		this.number = number;
	}

	/* (non-Javadoc)
	 * @see nl.eetmee.na.model.IPersonAddress#getLatitude()
	 */
	@Override
	public double getLatitude() {
		return latitude;
	}

	/* (non-Javadoc)
	 * @see nl.eetmee.na.model.IPersonAddress#setLatitude(double)
	 */
	@Override
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	/* (non-Javadoc)
	 * @see nl.eetmee.na.model.IPersonAddress#getLongtitude()
	 */
	@Override
	public double getLongitude() {
		return longtitude;
	}

	/* (non-Javadoc)
	 * @see nl.eetmee.na.model.IPersonAddress#setLongtitude(double)
	 */
	@Override
	public void setLongtitude(double longtitude) {
		this.longtitude = longtitude;
	}

	@Override
	public LatLng getLocation() {
		LatLng LatLng = new LatLng( person.getFullName(), latitude, longtitude );
		double avglat = (this.address.getLocation().getLatitude() + this.latitude)/2; 
		double avglng = (this.address.getLocation().getLongitude() + this.longtitude)/2; 
		address.setLocation(avglat, avglng );
		return LatLng;
	}

	@Override
	public void setLocation( double latitude, double longtitude) {
		this.latitude = latitude;
		this.longtitude = longtitude;
	}


	/* (non-Javadoc)
	 * @see nl.eetmee.na.model.IPersonAddress#getExtension()
	 */
	@Override
	public String getExtension() {
		return extension;
	}

	/* (non-Javadoc)
	 * @see nl.eetmee.na.model.IPersonAddress#setExtension(java.lang.String)
	 */
	@Override
	public void setExtension(String extension) {
		this.extension = extension;
	}

	@Override
	public long getAddressId() {
		return address.getAddressId();
	}

	@Override
	public String getStreet() {
		return address.getStreet();
	}

	@Override
	public void setStreet(String street) {
		this.address.setStreet(street);
	}

	@Override
	public String getStreetExtension() {
		return this.address.getStreetExtension();
	}

	@Override
	public String getHouseNumber() {
		return this.getNumber();
	}

	@Override
	public String getPostcode() {
		return this.address.getPostcode();
	}

	@Override
	public void setPostcode(String postcode) {
		this.address.setPostcode(postcode);
	}

	@Override
	public String getTown() {
		return this.address.getTown();
	}

	@Override
	public void setTown(String town) {
		this.address.setTown(town);
	}

	@Override
	public void setStreetExtension(String text) {
		this.address.setStreetExtension(text);
	}

	@Override
	public void setHousenumber(String text) {
		this.setNumber(text);
	}

	@Override
	public String getCountry() {
		return this.address.getCountry();
	}

	@Override
	public void setCountry(String country) {
		this.address.setCountry(country);
	}

	/**
	 * Returns true if the given postcode and number matches this one
	 * @param postcode
	 * @param number
	 * @return
	 */
	@Override
	public boolean isAddress( String postcode, String number ){
		return ( this.getPostcode().equals(postcode ) && this.getNumber().equals(number ));
	}

	/**
	 * Get a string id of the person address
	 * @return
	 */
	@Override
	public String toStringId(){
		return "[" + this.getPostcode() + ", " + this.number + "]";
	}

	@Override
	public String toString() {
		StringBuffer s = new StringBuffer();
		s.append(address.getStreet());
		s.append(" ");
		s.append(getNumber());
		s.append(", ");
		s.append(address.getPostcode()) ;
		s.append(" ");
		s.append(address.getTown());
		return s.toString();
	}

	@Override
	public String printStreet( boolean skipStreet ) {
		StringBuffer s = new StringBuffer();
		if(!skipStreet ){
			s.append(getStreet());
			s.append(" ,");
			s.append(getNumber());
			s.append(" ");
		}
		s.append(" [");
		s.append(getPostcode()) ;
		if( skipStreet ){
			s.append(", ");
			s.append(getNumber());
		}
		s.append("]");
		return s.toString();
	}

	@Override
	public void setCommunity(ICommunity community) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ICommunity getCommunity() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasValidLocation() {
		// TODO Auto-generated method stub
		return false;
	}
}
