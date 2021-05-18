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

import org.churuata.digital.core.location.IChuruataType;
import org.condast.commons.authentication.user.ILoginUser;

@Entity
public class ChuruataType implements Comparable<ChuruataType>, IChuruataType{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private long userid;

	private int type;
	
	private int contribution;
	
	private String description;
	
	@ManyToOne(cascade=CascadeType.PERSIST, fetch=FetchType.LAZY)
	private Churuata owner;

	@Basic(optional = false)
	@Column( nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;
	
	@Basic(optional = false)
	@Column( nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateDate;

	private transient ILoginUser user;
	
	
	public ChuruataType() {
		super();
		this.createDate = Calendar.getInstance().getTime();
		this.updateDate = Calendar.getInstance().getTime();
	}

	public ChuruataType( ILoginUser user, Types type) {
		this( user, type, Contribution.LOG );
	}

	public ChuruataType(ILoginUser user, Types type, String description) {
		this( user, type, description, Contribution.LOG);
	}

	public ChuruataType(ILoginUser user, Types type, Contribution contribution) {
		this( user, type, null, contribution);
	}

	public ChuruataType(ILoginUser user, Types type, String description, Contribution contribution) {
		super();
		this.type = type.ordinal();
		this.contribution = contribution.ordinal();
		this.description = description;
		this.user = user;
		this.userid = ( user == null )?-1:user.getId();
		this.createDate = Calendar.getInstance().getTime();
		this.updateDate = Calendar.getInstance().getTime();
	}

	public long getId() {
		return id;
	}

	public long getUserid() {
		return userid;
	}

	@Override
	public ILoginUser getUser() {
		return user;
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
	public Types getType() {
		return Types.values()[ type ];
	}

	@Override
	public Contribution getContribution() {
		return Contribution.values()[contribution];
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
		Types tp = getType();
		return tp.toString().compareTo(o.getType().toString());
	}
}
