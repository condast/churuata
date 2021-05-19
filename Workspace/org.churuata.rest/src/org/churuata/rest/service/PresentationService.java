package org.churuata.rest.service;

import java.util.Collection;

import javax.persistence.TypedQuery;

import org.churuata.digital.core.IPresentation.PresentationTypes;
import org.churuata.digital.core.location.IChuruata;
import org.churuata.rest.model.Presentation;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.persistence.service.AbstractEntityService;
import org.condast.commons.persistence.service.IPersistenceService;

public class PresentationService extends AbstractEntityService<Presentation>{

	public static final String S_QUERY_FIND_ALL = "SELECT p FROM Presentation p ";
	public static final String S_QUERY_FIND_LOCATION = S_QUERY_FIND_ALL + " WHERE p.churuatalatitude = :latitude AND p.longitude = :longitude ";
	public static final String S_QUERY_FIND_LOCATION_IN_RANGE = 
			"SELECT p FROM Presentation p WHERE p.latitude >= :latmin AND p.latitude <= :latmax AND "
			+ "p.longitude >= :lonmin AND p.longitude <= :lonmax ";

	public PresentationService( IPersistenceService service ) {
		super( Presentation.class, service );
	}

	public Presentation create( IChuruata churuata, PresentationTypes type, String title, String link ) {
		return this.create( churuata, type, title, link, null);
	}
	
	public Presentation create( IChuruata churuata, PresentationTypes type, String title, String link, String description ) {
		Presentation Presentation = new Presentation( churuata, type, title, link, description );
		super.create(Presentation);
		return Presentation;
	}

	public Collection<Presentation> findPresentation( long churuataId ){
		TypedQuery<Presentation> query = super.getTypedQuery( S_QUERY_FIND_LOCATION );
		return query.getResultList();
	}

	public Collection<Presentation> findPresentation( LatLng latlng, int range ){
		TypedQuery<Presentation> query = super.getTypedQuery( S_QUERY_FIND_LOCATION_IN_RANGE );
		query.setParameter("latmin", latlng.getLatitude() - range);
		query.setParameter("latmax", latlng.getLatitude() + range);
		query.setParameter("lonmin", latlng.getLongitude() - range);
		query.setParameter("lonmax", latlng.getLongitude() + range);
		return query.getResultList();
	}

}
