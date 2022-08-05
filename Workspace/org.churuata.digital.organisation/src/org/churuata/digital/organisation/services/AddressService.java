package org.churuata.digital.organisation.services;

import org.churuata.digital.organisation.core.Dispatcher;
import org.churuata.digital.organisation.model.Address;
import org.condast.commons.na.data.AddressData;
import org.condast.commons.persistence.service.AbstractEntityService;

public class AddressService extends AbstractEntityService<Address>{

	private static Dispatcher dispatcher = Dispatcher.getInstance();
	
	public AddressService() {
		super( Address.class, dispatcher );
	}

	public Address create( AddressData ad ) {
		Address address = new Address( ad );
		super.create(address);
		return address;
	}
	
	public static Address update( Address address,  AddressData ad ) {
		address.setStreet( ad.getStreet());
		address.setStreetExtension(ad.getStreetExtension());
		address.setNumber(ad.getHouseNumber());
		address.setPostcode(ad.getPostcode());
		address.setTown(ad.getTown());
		address.setCountry(ad.getCountry());
		address.setName(ad.getLocation().getId());
		address.setNumber(ad.getHouseNumber());
		address.setLocation(ad.getLocation().getLatitude(), ad.getLocation().getLongitude());
		return address;
	}

}
