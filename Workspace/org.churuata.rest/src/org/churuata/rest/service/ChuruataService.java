package org.churuata.rest.service;

import java.util.Collection;

import javax.persistence.TypedQuery;

import org.churuata.digital.core.location.IChuruataType;
import org.churuata.rest.model.Churuata;
import org.churuata.rest.model.Location;
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.persistence.service.AbstractEntityService;
import org.condast.commons.persistence.service.IPersistenceService;

public class ChuruataService extends AbstractEntityService<Churuata>{

	public static final String S_QUERY_FIND_ALL = "Churuata o ";
	public static final String S_QUERY_FIND_CHURUATA = S_QUERY_FIND_ALL + " WHERE o.latitude = :latitude AND o.longitude = :longitude ";
	public static final String S_QUERY_FIND_CHURUATA_IN_RANGE = 
			"SELECT l FROM Churuata l WHERE l.latitude >= :latmin AND l.latitude <= :latmax AND "
			+ "l.longitude >= :lonmin AND l.longitude <= :lonmax ";

	public ChuruataService( IPersistenceService service ) {
		super( Churuata.class, service );
	}

	public Churuata create( ILoginUser user, String name, String description, LatLng latlng, IChuruataType.Types type ) {
		LocationService ls = new LocationService(super.getService());
		Location location= ls.create(user, name, latlng);
		Churuata churuata = new Churuata( user, name, location );
		churuata.addType(user,  type );
		churuata.setDescription(description);
		super.create(churuata);
		return churuata;
	}

	public Collection<Churuata> findChuruata( LatLng latlng ){
		TypedQuery<Churuata> query = super.getTypedQuery( S_QUERY_FIND_CHURUATA );
		query.setParameter("latitude", latlng.getLatitude());
		query.setParameter("longitude", latlng.getLongitude());
		return query.getResultList();
	}

	public Collection<Churuata> findChuruata( LatLng latlng, int range ){
		TypedQuery<Churuata> query = super.getTypedQuery( S_QUERY_FIND_CHURUATA_IN_RANGE );
		query.setParameter("latmin", latlng.getLatitude() - range);
		query.setParameter("latmax", latlng.getLatitude() + range);
		query.setParameter("lonmin", latlng.getLongitude() - range);
		query.setParameter("lonmax", latlng.getLongitude() + range);
		return query.getResultList();
	}

}
