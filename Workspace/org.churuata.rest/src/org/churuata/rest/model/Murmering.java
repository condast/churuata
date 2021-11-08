package org.churuata.rest.model;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
import org.condast.commons.strings.StringUtils;

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
	
	private String contributor;
	
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

	public Murmering(IChuruata churuata, ILoginUser user, String text) {
		this( churuata, user.getUserName(), text );
	}
	
	public Murmering(IChuruata churuata, String contributor, String text) {
		super();
		this.text = text;
		this.churuata = (Churuata) churuata;
		this.contributor= StringUtils.isEmpty(contributor)?S_ANONYMOUS: contributor;
		this.createDate = Calendar.getInstance().getTime();
		this.updateDate = Calendar.getInstance().getTime();
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
	public String getContributor() {
		return contributor;
	}

	@Override
	public Date getCreateDate() {
		return createDate;
	}

	@Override
	public Date getUpdateDate() {
		return updateDate;
	}	
	
	@Override
	public Map<String, String> toAttributes(){
		Map<String, String> results = new HashMap<>();
		results.put(IMurmering.Attributes.TEXT.name(), text);
		results.put(IMurmering.Attributes.CONTRIBUTOR.name(), contributor);
		return results;
	}

}
