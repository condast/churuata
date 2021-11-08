package org.churuata.rest.model;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.condast.commons.IUpdateable;
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.data.latlng.LatLng;

@Entity
public class Location implements IUpdateable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private long userid;

	@Basic(optional = false)
	@Column( nullable=false)
	private String name;
	
	@Column( nullable=true)
	@Basic(optional = true)
	private String description;
	
	private double latitude;
	private double longitude;
	
	@Basic(optional = true)
	@Column( nullable=true)
	private String wkt;
		
	@Basic(optional = false)
	@Column( nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;
	
	@Basic(optional = false)
	@Column( nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateDate;

	public Location() {
		super();
	}

	public Location( ILoginUser user, LatLng latlng ) {
		this();
		this.userid = ( user == null )? -1: user.getId();
		this.name = latlng.getId();
		this.description = latlng.getDescription();
		this.latitude = latlng.getLatitude();
		this.longitude = latlng.getLongitude();
	}

	public long getId() {
		return id;
	}
	
	public long getUserId() {
		return userid;
	}

	public LatLng getLocation() {
		return new LatLng( this.name, this.latitude, this.longitude );
	}

	public void setLocation(LatLng location) {
		this.name = location.getId();
		this.description = location.getDescription();
		this.latitude = location.getLatitude();
		this.longitude = location.getLongitude();	
	}

	public String getWkt() {
		return wkt;
	}

	public void setWkt(String wkt) {
		this.wkt = wkt;
	}

	@Override
	public Date getCreateDate() {
		return createDate;
	}

	@Override
	public void setCreateDate(Date create) {
		this.createDate = create;
	}

	@Override
	public Date getUpdateDate() {
		return updateDate;
	}

	@Override
	public void setUpdateDate(Date update) {
		this.updateDate = update; 
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}
}
