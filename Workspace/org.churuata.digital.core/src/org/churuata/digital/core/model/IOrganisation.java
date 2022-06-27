package org.churuata.digital.core.model;

import org.churuata.digital.core.location.IChuruataService;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.na.model.IAddress;
import org.condast.commons.na.model.IContactPerson;

public interface IOrganisation {

	long getId();
	
	LatLng getLocation();

	public String getName();
	
	public String getDescription();
	
	public IAddress getAddress();
	
	public IContactPerson getContact();

	void setServices(IChuruataService[] services);

	int addService(IChuruataService service);

	void removeService(IChuruataService service);

	void removeService(String type, String value);

	void removeService(long serviceId);

	void clearServices();

	IChuruataService[] getServices();

	void addService( IChuruataService type, String value);

	int getServicesSize();

	String getWebsite();
}
