package org.satr.arnac.authentication.services;

import org.condast.commons.persistence.service.AbstractEntityService;
import org.satr.arnac.authentication.core.Dispatcher;
import org.satr.arnac.authentication.model.Address;

public class AddressService extends AbstractEntityService<Address>{

	private static Dispatcher dispatcher = Dispatcher.getInstance();
	
	public AddressService() {
		super( Address.class, dispatcher );
	}

	public Address create( String name, String password, String email ) {
		Address address = new Address();
		super.create(address);
		return address;
	}
}
