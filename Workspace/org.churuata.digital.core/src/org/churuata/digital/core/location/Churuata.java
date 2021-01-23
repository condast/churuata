package org.churuata.digital.core.location;

import java.util.Collection;
import java.util.TreeSet;

import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.data.latlng.LatLng;

public class Churuata implements Comparable<Churuata>{
	
	private ILoginUser owner;
	
	private String name, description;
	
	private LatLng location;
	
	private Collection<ChuruataTypes> types;

	public Churuata( LatLng location) {
		this( null, location.getId(), location );
	}
	
	public Churuata( ILoginUser owner, String name, LatLng location) {
		super();
		this.name = name;
		this.owner = owner;
		this.location = location;
		this.types = new TreeSet<>();
	}

	public ILoginUser getOwner() {
		return owner;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setTypes(Collection<ChuruataTypes> types) {
		this.types = types;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LatLng getLocation() {
		return location;
	}

	public boolean setType( ChuruataTypes type ) {
		this.types.clear();
		return this.types.add(type);
	}

	public boolean addType( ILoginUser user, ChuruataTypes.Types type ) {
		return this.types.add( new ChuruataTypes(this, type, user));
	}
	
	public boolean removeType( ChuruataTypes type ) {
		return this.types.remove(type);
	}

	public ChuruataTypes[] getTypes() {
		return types.toArray( new ChuruataTypes[ types.size()]);
	}

	@Override
	public int compareTo(Churuata o) {
		return this.name.compareTo(o.getName());
	}
}