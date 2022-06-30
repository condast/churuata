package org.churuata.digital.authentication.model;

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

import org.condast.commons.authentication.user.IAdmin;
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.strings.StringUtils;

@Entity
public class Admin implements IAdmin, Serializable {
	private static final long serialVersionUID = -4018348604104211097L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@Basic(optional = false)
	@Column( nullable=false)
	private long loginId;
	
	@Basic(optional = true)
	@Column( nullable=false)
	private String role;
	
	@Basic(optional = false)
	@Column( nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;
	
	@Basic(optional = false)
	@Column( nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateDate;

	private transient ILoginUser login;
	
	public Admin() {
		this.createDate = Calendar.getInstance().getTime();
		this.updateDate = this.createDate;
	}

	public Admin( ILoginUser user, Roles role) {
		this();
		this.role = role.name();
		this.loginId = this.login.getId();
		this.login = (Login) user;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public long getLoginId() {
		return this.loginId;
	}
	
	/* (non-Javadoc)
	 * @see org.fgf.animal.count.authentication.model.ILoginUser#getUserName()
	 */
	@Override
	public ILoginUser getUser() {
		return login;
	}

	public void setLogin(ILoginUser login) {
		this.login = login;
	}

	public Roles getRole() {
		return StringUtils.isEmpty(role)?Roles.GUEST: Roles.valueOf(role);
	}

	public void setRole( Roles role) {
		this.role = role.name();
	}

	@Override
	public Date getCreateDate() {
		return createDate;
	}

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
}