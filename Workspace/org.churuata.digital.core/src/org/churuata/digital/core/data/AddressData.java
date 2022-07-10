package org.churuata.digital.core.data;

import java.io.Serializable;
import java.math.BigDecimal;

import org.condast.commons.Utils;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.na.model.IAddress;

/**
 * The persistent class for the alg_tb_adres database table.
 * 
 */
public class AddressData implements IAddress,Serializable {
	private static final long serialVersionUID = 1L;

	private long addressId;
		
	private String town;
	
	private String postcode;
	
	private String street;
	
	private String street_ext;
	
	private String number;
	
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
	
	public AddressData(IAddress address) {
		this.addressId = address.getAddressId();
		this.street = address.getStreet();
		this.street_ext = address.getStreetExtension();
		this.number = address.getHouseNumber();
		this.postcode = address.getPostcode();
		this.town = address.getTown();
		this.country = address.getCountry();
		this.latitude = address.getLocation().getLatitude();
		this.longitude = address.getLocation().getLongitude();
	}

	public long getAddressId() {
		return this.addressId;
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

	public void setStreet(String street) {
		this.street = street;
	}

	@Override
	public void setNumber(String number) {
		this.number = number;
	}

	@Override
	public String getHouseNumber() {
		return number;
	}

	@Override
	public boolean hasValidLocation() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getStreetExtension() {
		return this.street_ext;
	}

	public void setStreetExtension(String street_ext) {
		this.street_ext = street_ext;
	}

	@Override
	public String getTown() {
		return this.town;
	}

	public void setTown(String town) {
		this.town = town;
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

	@Override
	public String printStreet(boolean skipStreet) {
		// TODO Auto-generated method stub
		return null;
	}
}