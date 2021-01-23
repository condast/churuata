package org.churuata.digital.core.location;

import org.condast.commons.data.latlng.LatLng;

public interface IChuruataCollection {

	boolean addChuruata(Churuata churuata);

	boolean removeChuruata(Churuata churuata);

	Churuata[] getChuruatas();

	Churuata[] getChuruatas( LatLng latlng, int distance );

	boolean contains(Churuata churuata);

}