package org.churuata.digital.organisation.services;

import java.util.Collection;
import java.util.List;

import javax.persistence.TypedQuery;

import org.churuata.digital.core.data.OrganisationData;
import org.churuata.digital.core.location.IChuruataType;
import org.churuata.digital.organisation.core.Dispatcher;
import org.churuata.digital.organisation.model.Organisation;
import org.condast.commons.na.model.IContactPerson;
import org.condast.commons.persistence.service.AbstractEntityService;

public class OrganisationService extends AbstractEntityService<Organisation>{

	public static final String S_QUERY_GET_ALL = "SELECT o FROM ORGANISATION o, PERSON c WHERE o.contact.id = :personid";

	private static Dispatcher dispatcher = Dispatcher.getInstance();
	
	public OrganisationService() {
		super( Organisation.class, dispatcher );
	}
	
	public Organisation create( IContactPerson person, String name, String description ) {
		Organisation organisation = new Organisation( name, description );
		super.create(organisation);
		return organisation;
	}

	public Organisation create( IContactPerson person, OrganisationData data ) {
		Organisation o = new Organisation( data.getName(), data.getDescription() );
		for( IChuruataType tp: data.getTypes()) {
			o.addService( tp);
		}		
		super.create(o);
		return o;
	}

	public Collection<Organisation> getAll( IContactPerson person ) {
		TypedQuery<Organisation> query = super.getTypedQuery( S_QUERY_GET_ALL );
		query.setParameter("personid", person.getContactId());
		List<Organisation> persons = query.getResultList();
		return persons;
	}
}
