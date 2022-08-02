package org.churuata.digital.organisation.services;

import java.util.Collection;

import javax.persistence.TypedQuery;

import org.churuata.digital.organisation.core.Dispatcher;
import org.churuata.digital.organisation.model.Location;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.persistence.service.AbstractEntityService;

public class LocationService extends AbstractEntityService<Location>{

	public static final String S_QUERY_FIND_ALL = S_SELECT_QUERY + "Location o ";
	public static final String S_QUERY_FIND_LOCATION = S_QUERY_FIND_ALL + " WHERE o.latitude = :latitude AND o.longitude = :longitude ";
	public static final String S_QUERY_FIND_LOCATION_IN_RANGE = S_QUERY_FIND_ALL + 
			"WHERE o.latitude >= :latmin AND o.latitude <= :latmax AND "
			+ "o.longitude >= :lonmin AND o.longitude <= :lonmax ";

	private static Dispatcher dispatcher = Dispatcher.getInstance();

	public LocationService( ) {
		super( Location.class, dispatcher );
	}

	public Location create( LatLng latlng ) {
		Location Location = new Location( latlng );
		super.create(Location);
		return Location;
	}

	public Collection<Location> findLocation( LatLng latlng ){
		TypedQuery<Location> query = super.getTypedQuery( S_QUERY_FIND_LOCATION );
		query.setParameter("latitude", latlng.getLatitude());
		query.setParameter("longitude", latlng.getLongitude());
		return query.getResultList();
	}

	public Collection<Location> findLocation( LatLng latlng, int range ){
		TypedQuery<Location> query = super.getTypedQuery( S_QUERY_FIND_LOCATION_IN_RANGE );
		query.setParameter("latmin", latlng.getLatitude() - range);
		query.setParameter("latmax", latlng.getLatitude() + range);
		query.setParameter("lonmin", latlng.getLongitude() - range);
		query.setParameter("lonmax", latlng.getLongitude() + range);
		return query.getResultList();
	}

}
