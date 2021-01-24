package org.churuata.rest.service;

import java.util.Collection;

import javax.persistence.TypedQuery;

import org.churuata.rest.model.Location;
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.persistence.service.AbstractEntityService;
import org.condast.commons.persistence.service.IPersistenceService;

public class LocationService extends AbstractEntityService<Location>{

	public static final String S_QUERY_FIND_ALL = "Location o ";
	public static final String S_QUERY_FIND_LOCATION = S_QUERY_FIND_ALL + " WHERE o.latitude = :latitude AND o.longitude = :longitude ";
	public static final String S_QUERY_FIND_LOCATION_IN_RANGE = 
			"SELECT l FROM Location l WHERE l.latitude >= :latmin AND l.latitude <= :latmax AND "
			+ "l.longitude >= :lonmin AND l.longitude <= :lonmax ";

	public LocationService( IPersistenceService service ) {
		super( Location.class, service );
	}

	public Location create( ILoginUser user, String name, LatLng latlng ) {
		Location Location = new Location( user, latlng );
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
