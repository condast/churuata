package org.churuata.digital.organisation.services;

import org.churuata.digital.core.location.IChuruataType;
import org.churuata.digital.organisation.core.Dispatcher;
import org.churuata.digital.organisation.model.Service;
import org.condast.commons.persistence.service.AbstractEntityService;

public class ServicesService extends AbstractEntityService<Service>{

	private static Dispatcher dispatcher = Dispatcher.getInstance();
	
	public ServicesService() {
		super( Service.class, dispatcher );
	}
	
	public Service createService( long id, IChuruataType.Types type, String value ) {
		Service service = new Service( id, type, value );
		super.create(service);
		return service;
	}

}
