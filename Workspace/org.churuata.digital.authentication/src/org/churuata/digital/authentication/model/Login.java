package org.churuata.digital.authentication.model;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.date.DateUtils;

@Entity
public class Login implements ILoginUser {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@Basic(optional = false)
	@Column( nullable=false)
	private String userName;
	
	@Basic(optional = true)
	@Column( nullable=false)
	private String password;
	
	@Basic(optional = true)
	@Column( nullable=true)
	private String email;
	
	private boolean registered;

	@Basic(optional = false)
	@Column( nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;
	
	@Basic(optional = false)
	@Column( nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateDate;

	/**
	 * security for communication
	 */
	private transient long security;
	
	private transient LatLng location;
	
	private transient boolean confirmed;
	
	public Login() {
		this.createDate = Calendar.getInstance().getTime();
		this.updateDate = this.createDate;
		security = new Random().nextLong();
		this.confirmed = false;
		this.registered = false;
	}

	@Override
	public long getId() {
		return id;
	}
	
	/* (non-Javadoc)
	 * @see org.fgf.animal.count.authentication.model.ILoginUser#getUserName()
	 */
	@Override
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	/* (non-Javadoc)
	 * @see org.fgf.animal.count.authentication.model.ILoginUser#getEmail()
	 */
	@Override
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public boolean isRegistered() {
		return registered;
	}

	@Override
	public void setRegistered(boolean registered) {
		this.registered = registered;
	}

	@Override
	public boolean isConfirmed() {
		return confirmed;
	}

	@Override
	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}

	@Override
	public LatLng getLocation() {
		return location;
	}

	@Override
	public void setLocation(double latitude, double longitude) {
		this.location = new LatLng( this.userName, latitude, longitude);
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
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
	public long getSecurity() {
		return security;
	}
	
	public void setSecurity(long security) {
		this.security = security;
	}

	@Override
	public boolean isCorrect(long userId, long security) {
		return (( this.id == userId ) && ( this.security == security));
	}

	/**
	 * returns true if the user has administrative privileges
	 * @param userName
	 * @param security
	 * @return
	 */
	@Override
	public boolean isAdmin( String userName, long security ) {
		return ( ILoginUser.S_ADMIN.equals(userName)) && ILoginUser.AdminToken == security;
	}

	@Override
	public String toString() {
		String date = DateUtils.getFormatted(this.createDate );
		return this.userName + "{" + date + "}";
	}

	@Override
	public int compareTo(ILoginUser arg0) {
		long diff = id - arg0.getId();
		return ( diff < 0)?-1: (diff>0)?1:0;
	}
}
