package org.churuata.rest.service;

import java.util.Collection;

import javax.persistence.TypedQuery;

import org.churuata.digital.core.location.IChuruata;
import org.churuata.rest.model.Murmering;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.persistence.service.AbstractEntityService;
import org.condast.commons.persistence.service.IPersistenceService;

public class MurmeringService extends AbstractEntityService<Murmering>{

	public static final String S_QUERY_FIND_ALL = "Murmering o ";
	public static final String S_QUERY_FIND_LOCATION = S_QUERY_FIND_ALL + " WHERE o.latitude = :latitude AND o.longitude = :longitude ";
	public static final String S_QUERY_FIND_LOCATION_IN_RANGE = 
			"SELECT l FROM Murmering l WHERE l.latitude >= :latmin AND l.latitude <= :latmax AND "
			+ "l.longitude >= :lonmin AND l.longitude <= :lonmax ";

	public MurmeringService( IPersistenceService service ) {
		super( Murmering.class, service );
	}
	
	public Murmering create( IChuruata churuata, String contributor, String text ) {
		Murmering Murmering = new Murmering( churuata, contributor, text );
		super.create(Murmering);
		return Murmering;
	}

	public Collection<Murmering> findMurmering( LatLng latlng ){
		TypedQuery<Murmering> query = super.getTypedQuery( S_QUERY_FIND_LOCATION );
		query.setParameter("latitude", latlng.getLatitude());
		query.setParameter("longitude", latlng.getLongitude());
		return query.getResultList();
	}

	public Collection<Murmering> findMurmering( LatLng latlng, int range ){
		TypedQuery<Murmering> query = super.getTypedQuery( S_QUERY_FIND_LOCATION_IN_RANGE );
		query.setParameter("latmin", latlng.getLatitude() - range);
		query.setParameter("latmax", latlng.getLatitude() + range);
		query.setParameter("lonmin", latlng.getLongitude() - range);
		query.setParameter("lonmax", latlng.getLongitude() + range);
		return query.getResultList();
	}

}
