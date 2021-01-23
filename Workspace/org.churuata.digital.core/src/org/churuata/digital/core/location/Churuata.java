package org.churuata.digital.core.location;

import java.util.Collection;
import java.util.TreeSet;

import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.data.latlng.LatLng;

public class Churuata implements Comparable<Churuata>, IChuruata{
	
	private ILoginUser owner;
	
	private String name, description;
	
	private LatLng location;
	
	private Collection<IChuruataType> types;

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

	@Override
	public ILoginUser getOwner() {
		return owner;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void setTypes(Collection<IChuruataType> types) {
		this.types = types;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public LatLng getLocation() {
		return location;
	}

	@Override
	public boolean setType( IChuruataType type ) {
		this.types.clear();
		return this.types.add(type);
	}

	@Override
	public boolean addType( ILoginUser user, ChuruataTypes.Types type ) {
		return this.types.add( new ChuruataTypes(type, user));
	}
	
	@Override
	public boolean removeType( IChuruataType type ) {
		return this.types.remove(type);
	}

	@Override
	public IChuruataType[] getTypes() {
		return types.toArray( new ChuruataTypes[ types.size()]);
	}

	@Override
	public int compareTo(Churuata o) {
		return this.name.compareTo(o.getName());
	}
}