package org.churuata.digital.core.location;

import java.util.Collection;
import java.util.TreeSet;

import org.churuata.digital.core.IPresentation;
import org.churuata.digital.core.location.IChuruataType.Contribution;
import org.churuata.digital.core.location.IChuruataType.Types;
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.data.latlng.LatLng;

public class Churuata implements Comparable<Churuata>, IChuruata{
	
	private ILoginUser owner;
	
	private String name, description;
	private String url;
	private LatLng location;

	private int logs;	
	private int maxlogs;
	
	private int leaves;
	private int maxLeaves;
	
	private Collection<IChuruataType> types;

	private Collection<IMurmering> murmerings;

	private Collection<String> presentation;

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
	public long getId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IChuruataType removeType(long typeId) {
		// TODO Auto-generated method stub
		return null;
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
	public String getHomepage() {
		return url;
	}

	@Override
	public void setHomepage(String url) {
		this.url = url;
	}

	@Override
	public boolean setType( IChuruataType type ) {
		this.types.clear();
		return this.types.add(type);
	}

	@Override
	public boolean removeType( IChuruataType type ) {
		return this.types.remove(type);
	}

	@Override
	public IChuruataType[] getTypes() {
		return types.toArray( new ChuruataType[ types.size()]);
	}

	@Override
	public int compareTo(Churuata o) {
		return this.name.compareTo(o.getName());
	}


	@Override
	public boolean removeMurmering(IMurmering murmering) {
		return false;
	}

	@Override
	public int getLogs() {
		return logs;
	}

	@Override
	public int getMaxLogs() {
		return this.maxlogs;
	}

	@Override
	public void setMaxLogs(int maxlogs) {
		this.maxlogs = maxlogs;
	}

	@Override
	public int getLeaves() {
		return this.leaves;
	}

	@Override
	public int getMaxLeaves() {
		return this.maxLeaves;
	}

	@Override
	public void setMaxLeaves(int maxLeaves) {
		this.maxLeaves = maxLeaves;
	}

	@Override
	public IChuruataType addType( String contributor, IChuruataType.Types type ) {
		ChuruataType ct = new ChuruataType( type, contributor );
		this.types.add( ct);
		return ct;
	}

	@Override
	public IChuruataType addType( String contributor, IChuruataType.Types type, IChuruataType.Contribution contribution ) {
		ChuruataType ct = new ChuruataType( type, contributor, contribution );
		this.types.add( ct);
		return ct;
	}
	@Override
	public IChuruataType removeType(String contributor, Types type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean addMurmering(IMurmering murmering) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeMurmering(String filter) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IMurmering[] getMurmerings() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean addPresentation(IPresentation presentation) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removePresentation(String title) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IPresentation[] getVideos() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPresentation[] getHammocks() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getNrOfVideos() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNrOfHammocks() {
		// TODO Auto-generated method stub
		return 0;
	}
}