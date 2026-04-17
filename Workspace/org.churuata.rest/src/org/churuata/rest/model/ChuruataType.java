package org.churuata.rest.model;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.churuata.digital.core.location.IChuruataService;
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.na.model.IAddress;

@Entity
public class ChuruataType implements Comparable<ChuruataType>, IChuruataService{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private String contributor;

	private int type;
	
	private int contribution;
	
	private String description;
	
	@ManyToOne(cascade=CascadeType.PERSIST, fetch=FetchType.LAZY)
	private Churuata churuata;

	@Basic(optional = false)
	@Column( nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date fromDate;
	
	@Basic(optional = false)
	@Column( nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date toDate;

	@Basic(optional = false)
	@Column( nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;
	
	@Basic(optional = false)
	@Column( nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateDate;
	
	public ChuruataType() {
		super();
		this.fromDate = Calendar.getInstance().getTime();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, 60);
		this.toDate = calendar.getTime();
		this.createDate = Calendar.getInstance().getTime();
		this.updateDate = Calendar.getInstance().getTime();
	}

	public ChuruataType( String contributor, Services type) {
		this( contributor, type, Contribution.LOG );
	}

	public ChuruataType( String contributor, Services type, String description) {
		this( contributor, type, description, Contribution.LOG);
	}

	public ChuruataType( String contributor, Services type, Contribution contribution) {
		this( contributor, type, null, contribution);
	}

	public ChuruataType(ILoginUser user, Services type, String description, Contribution contribution) {
		this( user.getUserName(), type, description, contribution ); 
	}
	
	public ChuruataType(String contributor, Services type, String description, Contribution contribution) {
		super();
		this.type = type.ordinal();
		this.contribution = contribution.ordinal();
		this.description = description;
		this.contributor = ( contributor == null )? S_ANONYMOUS:contributor;
		this.createDate = Calendar.getInstance().getTime();
		this.updateDate = Calendar.getInstance().getTime();
	}

	public long getId() {
		return id;
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
	public Services getService() {
		return Services.values()[ type ];
	}

	
	@Override
	public void setService(Services type) {
		this.contribution = type.ordinal();
	}

	@Override
	public Contribution getContribution() {
		return Contribution.values()[contribution];
	}

	@Override
	public void setContribution( Contribution contribution) {
		this.contributor = contribution.name();
	}

	@Override
	public Date from() {
		return this.fromDate;
	}
	
	@Override
	public void setFrom(Date date) {
		this.fromDate = date;
	}

	@Override
	public Date to() {
		return this.toDate;
	}

	@Override
	public void setTo(Date date) {
		this.toDate = date;
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

	@Override
	public int compareTo(ChuruataType o) {
		Services tp = getService();
		return tp.toString().compareTo(o.getService().toString());
	}

	@Override
	public LatLng getLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLocation(LatLng location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IAddress getAddress() {
		// TODO Auto-generated method stub
		return null;
	}
}
