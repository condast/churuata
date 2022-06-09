package org.churuata.digital.core.data;

import java.io.Serializable;
import java.math.BigDecimal;

import org.condast.commons.Utils;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.na.model.IAddress.AddressTypes;
import org.condast.commons.na.model.IAddress.Countries;

/**
 * The persistent class for the alg_tb_adres database table.
 * 
 */
public class AddressData implements Serializable {
	private static final long serialVersionUID = 1L;

	private long addressId;
		
	private String town;
	
	private String postcode;
	
	private String street;
	
	private String street_ext;
	
	private String country;
		
	private double longitude;
	private double latitude;
	
	public AddressData() {
		this( AddressTypes.MAIN );
	}

	public AddressData( AddressTypes type ) {
		this.country = Countries.THE_NETHERLANDS.name();
	}
	
	public AddressData( String straatnaam, String postcode, String plaats ){
		this.street = straatnaam;
		this.postcode = postcode;
		this.town = plaats;
		this.country = Countries.THE_NETHERLANDS.name();
	}
	
	public AddressData( String straatnaam, String postcode, 
			String plaats, BigDecimal lon, BigDecimal lat){
		this.street = straatnaam;
		this.postcode = postcode;
		this.town = plaats;
	}
	
	public long getAddressId() {
		return this.addressId;
	}

	public String getTown() {
		return this.town;
	}

	public String getPostcode() {
		return this.postcode;
	}

	public String getStreet() {
		return this.street;
	}

	public String getStreetExtension() {
		return this.street_ext;
	}

	public String getCountry() {
		return country;
	}

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
}