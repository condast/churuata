package org.churuata.digital.core.model;

import org.churuata.digital.core.model.IChuruataService.ServiceTypes;
import org.condast.commons.na.model.IAddress;
import org.condast.commons.na.model.IContactPerson;

public interface IOrganisation {

	long getId();

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

	IChuruataService[] getServiceTypes();

	void addService(ServiceTypes type, String value);

	int getServicesSize();
}
