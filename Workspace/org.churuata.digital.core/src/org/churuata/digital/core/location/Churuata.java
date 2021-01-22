package org.churuata.digital.core.location;

import java.util.Collection;
import java.util.TreeSet;

import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.strings.StringStyler;

public class Churuata {

	public enum Type{
		FOOD,
		SHELTER,
		MEDICINE,
		COMMUNITY,
		FAMILY;

		@Override
		public String toString() {
			return StringStyler.prettyString( name());
		}
	}
	
	private String name;
	
	private LatLng location;
	
	private Collection<Type> types;

	public Churuata(String name, LatLng location, Type type) {
		super();
		this.name = name;
		this.location = location;
		this.types = new TreeSet<>();
		this.types.add(type);
	}

	public String getName() {
		return name;
	}

	public LatLng getLocation() {
		return location;
	}
	
	public boolean addType( Type type ) {
		return this.types.add(type);
	}
	
	public boolean removeType( Type type ) {
		return this.types.remove(type);
	}

	public Type[] getTypes() {
		return types.toArray( new Type[ types.size()]);
	}
}