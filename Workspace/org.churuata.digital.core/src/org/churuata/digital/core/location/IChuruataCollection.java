package org.churuata.digital.core.location;

import org.condast.commons.data.latlng.LatLng;

public interface IChuruataCollection {

	boolean addChuruata(IChuruata churuata);

	boolean removeChuruata(IChuruata churuata);

	ChuruataData[] getChuruatas();

	IChuruata[] getChuruatas( LatLng latlng, int distance );

	boolean contains(IChuruata churuata);

}