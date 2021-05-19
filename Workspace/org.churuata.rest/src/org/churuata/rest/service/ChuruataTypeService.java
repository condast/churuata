package org.churuata.rest.service;

import java.util.Collection;

import javax.persistence.TypedQuery;

import org.churuata.digital.core.location.IChuruataType;
import org.churuata.rest.model.ChuruataType;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.persistence.service.AbstractEntityService;
import org.condast.commons.persistence.service.IPersistenceService;

public class ChuruataTypeService extends AbstractEntityService<ChuruataType>{

	public static final String S_QUERY_FIND_ALL = "Churuata o ";
	public static final String S_QUERY_FIND_CHURUATA = S_QUERY_FIND_ALL + " WHERE o.latitude = :latitude AND o.longitude = :longitude ";
	public static final String S_QUERY_FIND_CHURUATA_IN_RANGE = 
			"SELECT l FROM Churuata l WHERE l.latitude >= :latmin AND l.latitude <= :latmax AND "
			+ "l.longitude >= :lonmin AND l.longitude <= :lonmax ";

	public ChuruataTypeService( IPersistenceService service ) {
		super( ChuruataType.class, service );
	}

	public ChuruataType create( String contributor, IChuruataType.Types type, String description, IChuruataType.Contribution contribution ) {
		ChuruataType churuata = new ChuruataType( contributor, type, description, contribution );
		super.create(churuata);
		return churuata;
	}

	public Collection<ChuruataType> findChuruata( LatLng latlng ){
		TypedQuery<ChuruataType> query = super.getTypedQuery( S_QUERY_FIND_CHURUATA );
		query.setParameter("latitude", latlng.getLatitude());
		query.setParameter("longitude", latlng.getLongitude());
		return query.getResultList();
	}

	public Collection<ChuruataType> findChuruata( LatLng latlng, int range ){
		TypedQuery<ChuruataType> query = super.getTypedQuery( S_QUERY_FIND_CHURUATA_IN_RANGE );
		query.setParameter("latmin", latlng.getLatitude() - range);
		query.setParameter("latmax", latlng.getLatitude() + range);
		query.setParameter("lonmin", latlng.getLongitude() - range);
		query.setParameter("lonmax", latlng.getLongitude() + range);
		return query.getResultList();
	}

}
