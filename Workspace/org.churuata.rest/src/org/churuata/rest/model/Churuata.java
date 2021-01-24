package org.churuata.rest.model;

import java.util.Collection;
import java.util.Date;
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

import org.churuata.digital.core.location.IChuruata;
import org.churuata.digital.core.location.IChuruataType;
import org.churuata.digital.core.location.IMurmering;
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
	
	@OneToMany( mappedBy="owner", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<ChuruataType> types;

	@OneToMany( mappedBy="churuata", cascade = CascadeType.ALL, orphanRemoval = true)
	private Collection<Murmering> murmerings;

	@Basic(optional = false)
	@Column( nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;
	
	@Basic(optional = false)
	@Column( nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateDate;

	
	public Churuata() {
		super();
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
	}

	public Churuata( ILoginUser owner, String name, Location location) {
		super();
		this.name = name;
		this.owner = owner;
		this.ownerId = ( owner == null )?-1: owner.getId();
		this.location = location;
		this.types = new TreeSet<>();
	}

	@Override
	public long getId() {
		return id;
	}

	public long getOwnerId() {
		return ownerId;
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
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public LatLng getLocation() {
		return location.getLocation();
	}

	@Override
	public void setTypes(Collection<IChuruataType> types) {
		this.types.clear();
		for( IChuruataType ct: types )
			this.types.add( (ChuruataType) ct );
	}

	@Override
	public boolean setType( IChuruataType type ) {
		this.types.clear();
		return this.types.add((ChuruataType) type);
	}

	public boolean addType( ILoginUser user, ChuruataType type ) {
		return this.types.add( type );
	}

	@Override
	public boolean addType( ILoginUser user, IChuruataType.Types type ) {
		return this.types.add( new ChuruataType(user, type));
	}

	@Override
	public boolean addType( ILoginUser user, IChuruataType.Types type, IChuruataType.Contribution contribution ) {
		return this.types.add( new ChuruataType(user, type, contribution));
	}

	@Override
	public boolean removeType( IChuruataType type ) {
		return this.types.remove(type);
	}

	@Override
	public IChuruataType removeType( long typeId ) {
		for( ChuruataType type: this.types ) {
			if( type.getId() == typeId) {
				this.types.remove(type);
				return type;
			}
		}
		return null;
	}

	@Override
	public IChuruataType[] getTypes() {
		return types.toArray( new ChuruataType[ types.size()]);
	}

	@Override
	public boolean addMurmering( ILoginUser user, String text ) {
		return this.murmerings.add( new Murmering( this, user, text ));
	}

	@Override
	public boolean removeMurmering( IMurmering murmering ) {
		return this.murmerings.add( (Murmering) murmering );
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