package org.churuata.digital.core.model;

import org.churuata.digital.core.location.IChuruataType;
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

	void setServices(IChuruataType[] services);

	int addService(IChuruataType service);

	void removeService(IChuruataType service);

	void removeService(String type, String value);

	void removeService(long serviceId);

	void clearServices();

	IChuruataType[] getServiceTypes();

	void addService( IChuruataType type, String value);

	int getServicesSize();
}
