package org.churuata.digital.core.location;

import java.util.Collection;
import java.util.TreeSet;

import org.churuata.digital.core.IPresentation;
import org.churuata.digital.core.data.ChuruataOrganisationData;
import org.churuata.digital.core.data.ServiceData;
import org.churuata.digital.core.location.IChuruataService.Contribution;
import org.churuata.digital.core.location.IChuruataService.Services;
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.data.latlng.LatLng;

public class ChuruataData implements Comparable<ChuruataData>, IChuruata{
	
	private long id;
	private String name, description;
	private String url;
	private LatLng location;

	private int logs;	
	private int maxlogs;
	
	private int leaves;
	private int maxLeaves;
	
	private Collection<ServiceData> types;

	//private Collection<IMurmering> murmerings;

	//private Collection<String> presentation;

	public ChuruataData( LatLng location) {
		this( null, location.getId(), location );
	}

	public ChuruataData( IChuruata churuata) {
		super();
		this.name = churuata.getName();
		this.location = churuata.getLocation();
		this.types = new TreeSet<>();
	}

	public ChuruataData( ILoginUser owner, String name, LatLng location) {
		super();
		this.name = name;
		this.location = location;
		this.types = new TreeSet<>();
	}

	public ChuruataData( ChuruataOrganisationData organisation) {
		super();
		this.id = organisation.getId();
		this.name = organisation.getName();
		this.location = organisation.getLocation();
		this.types = new TreeSet<>();
		for( IChuruataService cs: organisation.getServices() )
			this.types.add( new ServiceData( cs ));
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public IChuruataService removeType(long typeId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
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
	public int getLeaves() {
		return this.leaves;
	}

	@Override
	public int getMaxLeaves() {
		return this.maxLeaves;
	}

	@Override
	public IChuruataService addType(String contributor, Services type) {
		ChuruataService ct =  new ChuruataService( type, contributor ); 
		return ct;
	}

	@Override
	public IChuruataService addType(String contributor, Services type, Contribution contribution) {
		ChuruataService ct =  new ChuruataService( type, contributor, contribution ); 
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