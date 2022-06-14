package org.churuata.digital.organisation.services;

import org.churuata.digital.core.data.OrganisationData;
import org.churuata.digital.core.location.IChuruataType;
import org.churuata.digital.organisation.core.Dispatcher;
import org.churuata.digital.organisation.model.Organisation;
import org.condast.commons.na.model.IContactPerson;
import org.condast.commons.persistence.service.AbstractEntityService;

public class OrganisationService extends AbstractEntityService<Organisation>{

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

}
