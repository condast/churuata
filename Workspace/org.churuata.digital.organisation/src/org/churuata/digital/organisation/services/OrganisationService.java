package org.churuata.digital.organisation.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.TypedQuery;

import org.churuata.digital.core.data.ChuruataOrganisationData;
import org.churuata.digital.core.model.IOrganisation;
import org.churuata.digital.organisation.core.Dispatcher;
import org.churuata.digital.organisation.model.Organisation;
import org.condast.commons.Utils;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.data.plane.Field;
import org.condast.commons.data.plane.IField;
import org.condast.commons.na.model.IContactPerson;
import org.condast.commons.persistence.service.AbstractEntityService;

public class OrganisationService extends AbstractEntityService<Organisation>{

	public static final String S_QUERY_SELECT = "SELECT o FROM ORGANISATION o";
	public static final String S_QUERY_GET_ALL = S_QUERY_SELECT + ", PERSON c WHERE o.contact.id = :personid";
	public static final String S_QUERY_FIND_PRIMARY = S_QUERY_GET_ALL + " AND o.principal = true";

	public static final String S_QUERY_VERIFIED = " WHERE o.verified = :choice";

	public static final String S_QUERY_FIND_IN_RANGE = S_QUERY_SELECT +
			" WHERE o.latitude >= :latmin AND o.latitude <= :latmax AND "
			+ "o.longitude >= :lonmin AND o.longitude <= :lonmax ";

	private static Dispatcher dispatcher = Dispatcher.getInstance();
	
	public OrganisationService() {
		super( Organisation.class, dispatcher );
	}
	
	@Override
	public List<Organisation> findAll() {
		return super.query( S_QUERY_SELECT);
	}

	public List<IOrganisation> findAll( IOrganisation.Verification verification ) {
		String queryStr = S_QUERY_SELECT;
		TypedQuery<Organisation> query = null;
		switch( verification) {
		case VERIFIED:
			queryStr += S_QUERY_VERIFIED;
			query = super.getTypedQuery( queryStr );
			query.setParameter("choice", true);
			break;
		case NOT_VERIFIED:
			query = super.getTypedQuery( queryStr );
			query.setParameter("choice", false);
			break;
		default:
			query = super.getTypedQuery( queryStr );			
			break;
		}
		List<Organisation> results = query.getResultList();
		return new ArrayList<IOrganisation>( results );
	}

	public Organisation create( IContactPerson person, String name, String description, boolean primary ) {
		Organisation organisation = new Organisation( name, description, primary );
		super.create(organisation);
		return organisation;
	}

	public Organisation create( IContactPerson person, ChuruataOrganisationData data ) {
		Organisation o = new Organisation( person, data );
		super.create(o);
		return o;
	}

	public Collection<Organisation> getAll( IContactPerson person ) {
		TypedQuery<Organisation> query = super.getTypedQuery( S_QUERY_GET_ALL );
		query.setParameter("personid", person.getContactId());
		List<Organisation> organisation = query.getResultList();
		return organisation;
	}

	public Organisation findPrincipal( IContactPerson person ) {
		TypedQuery<Organisation> query = super.getTypedQuery( S_QUERY_FIND_PRIMARY );
		query.setParameter("personid", person.getContactId());
		List<Organisation> organisations = query.getResultList();
		return Utils.assertNull(organisations)?null: organisations.iterator().next();
	}

	public List<IOrganisation> getAll( double latitude, double longitude, int range ) {
		TypedQuery<Organisation> query = super.getTypedQuery( S_QUERY_FIND_IN_RANGE );
		LatLng location = new LatLng( latitude, longitude );
		IField field = new Field( location, range, range );
		LatLng shift = field.move(-range/2, -range/2);
		field = new Field( shift, range, range );
		query.setParameter("latmin", field.getCoordinates().getLatitude() );
		query.setParameter("lonmax", field.getCoordinates().getLongitude() );
		query.setParameter("latmax", field.getBottomRight().getLatitude() );
		query.setParameter("lonmin", field.getBottomRight().getLongitude() );
		List<Organisation> organisations = query.getResultList();
		return new ArrayList<IOrganisation>( organisations );
	}	
	
	public static ChuruataOrganisationData[] toOrganisationData( Collection<IOrganisation> input ){
		Collection<ChuruataOrganisationData> results = new ArrayList<>();
		if( Utils.assertNull(input))
			return results.toArray( new ChuruataOrganisationData[ results.size()]);
		input.forEach( o->{ results.add(new ChuruataOrganisationData( o ));});
		return results.toArray( new ChuruataOrganisationData[ results.size()]);
	}
}
