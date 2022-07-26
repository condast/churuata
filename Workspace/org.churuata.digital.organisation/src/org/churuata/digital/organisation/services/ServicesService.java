package org.churuata.digital.organisation.services;

import org.churuata.digital.core.data.ServiceData;
import org.churuata.digital.core.location.IChuruataService;
import org.churuata.digital.organisation.core.Dispatcher;
import org.churuata.digital.organisation.model.Service;
import org.condast.commons.persistence.service.AbstractEntityService;

public class ServicesService extends AbstractEntityService<Service>{

	private static Dispatcher dispatcher = Dispatcher.getInstance();
	
	public ServicesService() {
		super( Service.class, dispatcher );
	}

	public Service createService( ServiceData data ) {
		Service service = new Service( data.getService(), null);
		service.setDescription(data.getDescription());
		service.setFrom(data.from());
		service.setTo(data.to());
		super.create(service);
		return service;
	}

	public Service createService( IChuruataService.Services type, String value ) {
		Service service = new Service();
		service.setService(type);
		super.create(service);
		return service;
	}

	public Service update(ServiceData serviceData) {
		Service service = find( serviceData.getId());
		service.setService( serviceData.getService());
		service.setDescription(serviceData.getDescription());
		service.setFrom( serviceData.from());
		service.setTo( serviceData.to());
		service.setContribution(serviceData.getContribution());
		super.update(service);
		return service;
	}

}
