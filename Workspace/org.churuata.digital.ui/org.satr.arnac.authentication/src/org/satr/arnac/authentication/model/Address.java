package org.satr.arnac.authentication.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.condast.commons.Utils;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.na.model.IAddress;

/**
 * The persistent class for the alg_tb_adres database table.
 * 
 */
@Entity
public class Address implements IAddress, Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="ADDRESS_ID", nullable=false)
	private long addressId;
		
	@Column(nullable=false)
	private String town;
	
	@Column(nullable=false)
	private String postcode;
	
	@Column(nullable=false)
	private String street;
	
	private String street_ext;
	
	@Column(nullable=false)
	private String country;
		
	@OneToMany( mappedBy="address", cascade = CascadeType.ALL, fetch=FetchType.EAGER)
	private Collection<PersonAddress> locations;
	
	private double longitude;
	private double latitude;
	
	public Address() {
		this( AddressTypes.MAIN );
	}

	public Address( AddressTypes type ) {
		locations = new ArrayList<PersonAddress>();
		this.country = Countries.THE_NETHERLANDS.name();
	}
	
	public Address( String straatnaam, String postcode, String plaats ){
		this.street = straatnaam;
		this.postcode = postcode;
		this.town = plaats;
		this.country = Countries.THE_NETHERLANDS.name();
	}
	
	public Address( String straatnaam, String postcode, 
			String plaats, BigDecimal lon, BigDecimal lat){
		this.street = straatnaam;
		this.postcode = postcode;
		this.town = plaats;
	}
	
	@Override
	public long getAddressId() {
		return this.addressId;
	}

	public void setAddressId( long adresId) {
		this.addressId = adresId;
	}

	public void addPersonAddress( PersonAddress pa ){
		this.locations.add( pa );
	}

	public void removePersonAddress( PersonAddress pa ){
		this.locations.remove( pa );
	}

	@Override
	public String getTown() {
		return this.town;
	}

	@Override
	public void setTown(String plaats) {
		this.town = plaats;
	}

	@Override
	public String getPostcode() {
		return this.postcode;
	}

	@Override
	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	@Override
	public String getStreet() {
		return this.street;
	}

	@Override
	public void setStreet(String street) {
		this.street = street;
	}

	@Override
	public String getStreetExtension() {
		return this.street_ext;
	}

	@Override
	public void setStreetExtension(String street_ext) {
		this.street_ext = street_ext;
	}

	@Override
	public String getCountry() {
		return country;
	}

	@Override
	public void setCountry(String country) {
		this.country = country;
	}

	@Override
	public LatLng getLocation() {
		return new LatLng( this.toString(), this.latitude, this.longitude );
	}
	
	public void setLocation( double latitude, double longitude ){
		this.latitude = latitude;
		this.longitude = longitude;
	}

	protected boolean hasText( String text ){
		return Utils.assertNull( text );
	}
	
	@Override
	public String toString() {
		StringBuffer s = new StringBuffer();
		s.append(getStreet());
		s.append(" ");
		s.append(", ");
		s.append(getPostcode()) ;
		s.append(" ");
		s.append(getTown());
		return s.toString();
	}

	@Override
	public String printStreet( boolean skipStreet ) {
		StringBuffer s = new StringBuffer();
		if(!skipStreet ){
			s.append(getStreet());
		}
		s.append(" [");
		s.append(getPostcode()) ;
		if(skipStreet ){
			s.append("]");
		}
		return s.toString();
	}
}