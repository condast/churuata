package org.churuata.digital.core.model;

import org.churuata.digital.core.location.IChuruataService;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.na.model.IAddress;
import org.condast.commons.na.model.IContactPerson;
import org.condast.commons.strings.StringStyler;
import org.condast.commons.strings.StringUtils;

public interface IOrganisation {

	public enum OrganisationTypes{
		UNKNOWN(0),
		PRIVATE(1),
		GOVERNMENT(2),
		INTERNATIONAL(3);
		
		private int index;

		private OrganisationTypes(int index) {
			this.index = index;
		}

		public int getIndex() {
			return index;
		}
		
		@Override
		public String toString() {
			return StringStyler.prettyString( name());
		}	
	}

	public enum Verification{
		UNKNOWN,
		ALL,
		VERIFIED,
		NOT_VERIFIED;
		
		@Override
		public String toString() {
			return StringStyler.prettyString( name());
		}	
		
		public static Verification getVerification( String arg0 ) {
			return StringUtils.isEmpty(arg0)?Verification.ALL: Verification.valueOf(arg0);
		}
	}

	long getId();
	
	LatLng getLocation();

	public String getName();
	
	public String getDescription();
	
	public IAddress getAddress();
	
	public IContactPerson getContact();
	
	public OrganisationTypes getType();
	
	public boolean isVerified();
	
	public int getScore();

	void setServices(IChuruataService[] services);

	int addService(IChuruataService service);

	void removeService(IChuruataService service);

	void removeService(String type, String value);

	void removeService(long serviceId);

	void clearServices();

	IChuruataService[] getServices();

	int getServicesSize();

	String getWebsite();
}
