package org.churuata.rest.model;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.churuata.digital.core.location.IChuruata;
import org.churuata.digital.core.location.IMurmering;
import org.condast.commons.authentication.user.ILoginUser;

@Entity
public class Murmering implements IMurmering {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Basic(optional = false)
	@Column( nullable=false)
	private String text;
	
	@JoinColumn(name="CHURUATA_ID", nullable=false)
	@OneToOne
	private Churuata churuata;
	
	private long userId;
	
	private transient ILoginUser user;
	
	@Basic(optional = false)
	@Column( nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;
	
	@Basic(optional = false)
	@Column( nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateDate;

	public Murmering() {
		super();
	}

	public Murmering(Churuata churuata, ILoginUser user, String text) {
		super();
		this.text = text;
		this.churuata = churuata;
		this.user = user;
		this.userId = this.user.getId();
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public void setText(String text) {
		this.text = text;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public IChuruata getChuruata() {
		return churuata;
	}

	@Override
	public long getUserId() {
		return userId;
	}

	@Override
	public Date getCreateDate() {
		return createDate;
	}

	@Override
	public Date getUpdateDate() {
		return updateDate;
	}
}
