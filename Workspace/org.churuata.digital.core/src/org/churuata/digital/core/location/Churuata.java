package org.churuata.digital.core.location;

import java.util.Collection;
import java.util.TreeSet;

import org.churuata.digital.core.IPresentation;
import org.churuata.digital.core.location.IChuruataService.Services;
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
	
	private Collection<IChuruataService> types;

	//private Collection<IMurmering> murmerings;

	//private Collection<String> presentation;

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
	public IChuruataService removeType(long typeId) {
		// TODO Auto-generated method stub
		return null;
	}

	public ILoginUser getOwner() {
		return owner;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setTypes(Collection<IChuruataService> types) {
		this.types = types;
	}

	@Override
	public String getDescription() {
		return description;
	}

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

	public void setHomepage(String url) {
		this.url = url;
	}

	public boolean setType( IChuruataService type ) {
		this.types.clear();
		return this.types.add(type);
	}

	@Override
	public boolean removeType( IChuruataService type ) {
		return this.types.remove(type);
	}

	@Override
	public IChuruataService[] getTypes() {
		return types.toArray( new ChuruataService[ types.size()]);
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

	public void setMaxLeaves(int maxLeaves) {
		this.maxLeaves = maxLeaves;
	}

	@Override
	public IChuruataService addType( String contributor, IChuruataService.Services type ) {
		ChuruataService ct = new ChuruataService( type, contributor );
		this.types.add( ct);
		return ct;
	}

	@Override
	public IChuruataService addType( String contributor, IChuruataService.Services type, IChuruataService.Contribution contribution ) {
		ChuruataService ct = new ChuruataService( type, contributor, contribution );
		this.types.add( ct);
		return ct;
	}
	@Override
	public IChuruataService removeType(String contributor, Services type) {
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