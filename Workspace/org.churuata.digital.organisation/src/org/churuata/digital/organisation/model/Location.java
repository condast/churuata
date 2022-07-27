package org.churuata.digital.organisation.model;

import java.io.Serializable;
import java.util.Calendar;
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
import org.condast.commons.data.latlng.ILocation;
import org.condast.commons.data.latlng.LatLng;

@Entity
public class Location implements ILocation, IUpdateable, Serializable {
	private static final long serialVersionUID = 82849911378450887L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Basic(optional = false)
	@Column( nullable=false)
	private String name;

	@Column( nullable=true)
	@Basic(optional = true)
	private String description;

	private double latitude;
	private double longitude;

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

	public Location( LatLng latlng ) {
		this();
		this.name = latlng.getId();
		this.description = latlng.getDescription();
		this.latitude = latlng.getLatitude();
		this.longitude = latlng.getLongitude();
	}

	@Override
	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public LatLng getLocation() {
		return new LatLng( this.name, this.description, this.latitude, this.longitude );
	}

	public void setLocation(LatLng location) {
		this.name = location.getId();
		this.description = location.getDescription();
		this.latitude = location.getLatitude();
		this.longitude = location.getLongitude();
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
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
	public int compareTo(ILocation o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void update() {
		Calendar calendar = Calendar.getInstance();
		this.updateDate = calendar.getTime();	
	}
}
