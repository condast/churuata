package org.churuata.rest.model;

import java.util.Collection;
import java.util.Date;
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
	
	@OneToMany( mappedBy="batch", cascade = CascadeType.ALL, orphanRemoval = true)
	private TreeSet<ChuruataType> types;

	@Basic(optional = false)
	@Column( nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;
	
	@Basic(optional = false)
	@Column( nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateDate;

	public Churuata( LatLng location) {
		this( null, location.getId(), location );
	}
	
	public Churuata( ILoginUser owner, String name, LatLng location) {
		super();
		this.name = name;
		this.owner = owner;
		this.ownerId = owner.getId();
		this.location = new Location( owner, location, 12 );
		this.types = new TreeSet<>();
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
	public void setTypes(Collection<IChuruataType> types) {
		this.types.clear();
		for( IChuruataType ct: types )
			this.types.add( (ChuruataType) ct );
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
	public boolean setType( IChuruataType type ) {
		this.types.clear();
		return this.types.add((ChuruataType) type);
	}

	@Override
	public boolean addType( ILoginUser user, IChuruataType.Types type ) {
		return this.types.add( new ChuruataType(type, user));
	}
	
	@Override
	public boolean removeType( IChuruataType type ) {
		return this.types.remove(type);
	}

	@Override
	public IChuruataType[] getTypes() {
		return types.toArray( new ChuruataType[ types.size()]);
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