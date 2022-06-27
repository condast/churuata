package org.churuata.digital.core.location;

import java.util.Arrays;
import java.util.Collection;
import java.util.TreeSet;

import org.churuata.digital.core.IPresentation;
import org.churuata.digital.core.location.IChuruataService.Contribution;
import org.churuata.digital.core.location.IChuruataService.Services;
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.data.latlng.LatLng;

public class ChuruataData implements Comparable<ChuruataData>, IChuruata{
	
	private ILoginUser owner;
	
	private String name, description;
	private String url;
	private LatLng location;

	private int logs;	
	private int maxlogs;
	
	private int leaves;
	private int maxLeaves;
	
	private Collection<ChuruataService> types;

	//private Collection<IMurmering> murmerings;

	//private Collection<String> presentation;

	public ChuruataData( LatLng location) {
		this( null, location.getId(), location );
	}

	public ChuruataData( IChuruata churuata) {
		super();
		this.name = churuata.getName();
		this.owner = churuata.getOwner();
		this.location = churuata.getLocation();
		this.types = new TreeSet<>();
		this.setTypes( Arrays.asList( churuata.getTypes()));
	}

	public ChuruataData( ILoginUser owner, String name, LatLng location) {
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
	public void setTypes(Collection<IChuruataService> types) {
		for( IChuruataService tp: types) {
			ChuruataService ct = new ChuruataService( tp.getService(), tp.getContributor(), tp.getContribution());
			ct.setFrom(tp.from());
			ct.setTo(tp.to());
			this.types.add(ct );
		}
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
	public boolean setType( IChuruataService type ) {
		this.types.clear();
		ChuruataService ct = new ChuruataService( type.getService(), type.getContributor(), type.getContribution());
		ct.setFrom(type.from());
		ct.setTo(type.to());
		this.types.add(ct );
		return this.types.add(ct);
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
	public int compareTo(ChuruataData o) {
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
	public IChuruataService addType(String contributor, Services type) {
		ChuruataService ct =  new ChuruataService( type, contributor ); 
		types.add(ct);
		return ct;
	}

	@Override
	public IChuruataService addType(String contributor, Services type, Contribution contribution) {
		ChuruataService ct =  new ChuruataService( type, contributor, contribution ); 
		types.add( ct);
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