package org.churuata.digital.organisation.services;

import org.churuata.digital.organisation.core.Dispatcher;
import org.churuata.digital.organisation.model.Contact;
import org.condast.commons.na.model.IContact.ContactTypes;
import org.condast.commons.persistence.service.AbstractEntityService;

public class ContactService extends AbstractEntityService<Contact>{

	private static Dispatcher dispatcher = Dispatcher.getInstance();
	
	public ContactService() {
		super( Contact.class, dispatcher );
	}
	
	public Contact createContact( ContactTypes type, String value ) {
		Contact contact = new Contact( type, value );
		super.create(contact);
		return contact;
	}

}
