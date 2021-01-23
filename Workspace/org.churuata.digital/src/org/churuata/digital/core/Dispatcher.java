package org.churuata.digital.core;

import java.util.ArrayList;
import java.util.Collection;
import org.churuata.digital.core.location.Churuata;
import org.churuata.digital.core.location.IChuruata;
import org.churuata.digital.core.location.IChuruataCollection;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.data.latlng.LatLngUtils;

public class Dispatcher implements IChuruataCollection {

	private static Dispatcher dispatcher = new Dispatcher();
	
	private Collection<Churuata> churuatas;
	
	public Dispatcher() {
		super();
		churuatas = new ArrayList<>();
	}

	public static Dispatcher getInstance() {
		return dispatcher;
	}
	
	@Override
	public boolean contains( IChuruata churuata ) {
		return this.churuatas.contains(churuata);
	}
	
	@Override
	public boolean addChuruata(  Churuata churuata ) {
		return this.churuatas.add(churuata);
	}

	@Override
	public boolean removeChuruata(  IChuruata churuata ) {
		return this.churuatas.remove(churuata);
	}
	
	@Override
	public Churuata[] getChuruatas() {
		return this.churuatas.toArray( new Churuata[ this.churuatas.size()]);
	}

	@Override
	public IChuruata[] getChuruatas(LatLng latlng, int distance) {
		Collection<Churuata> results = new ArrayList<>();
		for( Churuata churuata: this.churuatas) {
			if( LatLngUtils.distance( churuata.getLocation(), latlng) < distance )
				results.add(churuata);
		}
		return results.toArray( new Churuata[ results.size() ]);
	}

	
}
