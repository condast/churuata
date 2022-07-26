package org.churuata.rest.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.churuata.digital.core.IPresentation;
import org.churuata.digital.core.location.IChuruata;
import org.churuata.digital.core.location.IChuruataService;
import org.churuata.digital.core.location.IChuruataService.Contribution;
import org.churuata.digital.core.location.IMurmering;
import org.condast.commons.Utils;
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.data.latlng.LatLng;

@Entity
public class Churuata implements Comparable<Churuata>, IChuruata{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private long ownerId;

	private transient ILoginUser owner;
	
	@Basic(optional = false)
	@Column( nullable=false)
	private String name;
	
	@Basic(optional = false)
	@Column( nullable=false)
	private String description;
	
	private Location location;
	private String url;
	
	private int maxlogs;
	
	private int maxLeaves;
	
	@OneToMany( mappedBy="churuata", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<ChuruataType> types;

	@OneToMany( mappedBy="churuata", cascade = CascadeType.ALL, orphanRemoval = true)
	private Collection<Murmering> murmerings;

	@OneToMany( mappedBy="churuata", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Presentation> presentations;

	@Basic(optional = false)
	@Column( nullable=true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;
	
	@Basic(optional = false)
	@Column( nullable=true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateDate;

	
	public Churuata() {
		super();
		this.createDate = Calendar.getInstance().getTime();
		this.updateDate = Calendar.getInstance().getTime();
	}

	public Churuata( LatLng location) {
		this( null, location.getId(), location );
	}

	public Churuata( String name, LatLng location) {
		this( null, name, location );
	}
	
	public Churuata( ILoginUser owner, String name, LatLng location) {
		super();
		this.name = name;
		this.owner = owner;
		this.ownerId = ( owner == null )?-1: owner.getId();
		this.location = new Location( owner, location );
		this.types = new TreeSet<>();
		this.presentations = new ArrayList<>();
		this.createDate = Calendar.getInstance().getTime();
		this.updateDate = Calendar.getInstance().getTime();
	}

	public Churuata( ILoginUser owner, String name, Location location) {
		super();
		this.name = name;
		this.owner = owner;
		this.ownerId = ( owner == null )?-1: owner.getId();
		this.location = location;
		this.types = new TreeSet<>();
		this.createDate = Calendar.getInstance().getTime();
		this.updateDate = Calendar.getInstance().getTime();
	}

	@Override
	public long getId() {
		return id;
	}

	public ILoginUser getOwner() {
		return owner;
	}

	public long getOwnerId() {
		return ownerId;
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
		return location.getLocation();
	}

	@Override
	public String getHomepage() {
		return url;
	}

	@Override
	public int getLogs() {
		int logs = 0;
		for( ChuruataType type: this.types ) {
			if( Contribution.LOG.equals( type.getContribution())) {
				logs++;
			}
		}
		return logs;
	}

	@Override
	public int getMaxLogs() {
		return maxlogs;
	}

	@Override
	public int getLeaves() {
		int leaves = 0;
		for( ChuruataType type: this.types ) {
			if( Contribution.LEAF.equals( type.getContribution())) {
				leaves++;
			}
		}
		return leaves;
	}

	@Override
	public int getMaxLeaves() {
		return maxLeaves;
	}

	public boolean addType( ChuruataType type ) {
		return this.types.add( type );
	}

	@Override
	public IChuruataService addType( String contributor, IChuruataService.Services type ) {
		ChuruataType ct = new ChuruataType( contributor, type );
		this.types.add( ct);
		return ct;
	}

	@Override
	public IChuruataService addType( String contributor, IChuruataService.Services type, IChuruataService.Contribution contribution ) {
		ChuruataType ct = new ChuruataType( contributor, type, contribution );
		this.types.add( ct);
		return ct;
	}

	@Override
	public boolean removeType( IChuruataService type ) {
		return this.types.remove(type);
	}

	@Override
	public IChuruataService removeType( long typeId ) {
		for( ChuruataType type: this.types ) {
			if( type.getId() == typeId) {
				this.types.remove(type);
				return type;
			}
		}
		return null;
	}

	@Override
	public IChuruataService removeType( String contributor, IChuruataService.Services type ) {
		Collection<ChuruataType> temp = new ArrayList<>( this.types);
		for( ChuruataType ct: temp ) {
			if( ct.getContribution().equals(contributor) && ct.getService().equals(type)) {
				this.types.remove(ct);
				return ct;
			}
		}
		return null;
	}

	@Override
	public boolean addPresentation( IPresentation presentation ) {
		return this.presentations.add( (Presentation) presentation );
	}

	@Override
	public boolean removePresentation( String title ) {
		return this.presentations.removeIf( p -> p.getTitle().equals(title));
	}
	
	@Override
	public IPresentation[] getVideos() {
		Collection<IPresentation> results = new ArrayList<>();
		if( Utils.assertNull(presentations))
			return results.toArray( new IPresentation[ results.size()]);
		for( Presentation presentation: presentations){
			if( IPresentation.PresentationTypes.VIDEO.equals(presentation.getType()))
				results.add(presentation);
		}
		return results.toArray( new IPresentation[ results.size()]);
	}

	@Override
	public IPresentation[] getHammocks() {
		Collection<IPresentation> results = new ArrayList<>();
		for( Presentation presentation: presentations){
			if( IPresentation.PresentationTypes.HAMMOCK.equals(presentation.getType()))
				results.add(presentation);
		}
		return results.toArray( new IPresentation[ results.size()]);
	}

	@Override
	public int getNrOfVideos() {
		return (int) this.presentations.stream().filter( p-> IPresentation.PresentationTypes.VIDEO.equals(p.getType())).count();
	}

	@Override
	public int getNrOfHammocks() {
		return (int) this.presentations.stream().filter( p-> IPresentation.PresentationTypes.HAMMOCK.equals(p.getType())).count();
	}

	@Override
	public IChuruataService[] getTypes() {
		return types.toArray( new ChuruataType[ types.size()]);
	}

	@Override
	public boolean addMurmering( IMurmering murmering ) {
		return this.murmerings.add(  (Murmering) murmering );
	}

	@Override
	public boolean removeMurmering( IMurmering murmering ) {
		return this.murmerings.add( (Murmering) murmering );
	}

	@Override
	public boolean removeMurmering( String filter ) {
		return this.murmerings.removeIf( m->m.getText().contains(filter));
	}

	@Override
	public IMurmering[] getMurmerings() {
		return this.murmerings.toArray( new IMurmering[ types.size()]);
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date create) {
		this.createDate = create;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date update) {
		this.updateDate = update; 
	}

	public int compareTo(Churuata o) {
		return this.name.compareTo(o.getName());
	}
}