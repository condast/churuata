package org.satr.arnac.authentication.model;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.date.DateUtils;
import org.condast.commons.strings.StringUtils;

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

	@Basic(optional = false)
	@Column( nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;
	
	@Basic(optional = false)
	@Column( nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateDate;

	@Basic(optional = true)
	@OneToOne
	private Person person; 
	
	/**
	 * token for communication
	 */
	private transient long token;
	
	private transient LatLng location;
	
	public Login() {
		this.createDate = Calendar.getInstance().getTime();
		this.updateDate = this.createDate;
		token = new Random().nextLong();
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
	public LatLng getLocation() {
		return location;
	}

	@Override
	public void setLocation(double latitude, double longitude) {
		this.location = new LatLng( this.userName, latitude, longitude);
	}

	public Person getPerson() {
		return person;
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
	public long getToken() {
		return token;
	}
	
	@Override
	public void setToken(long token) {
		this.token = token;
	}

	@Override
	public boolean isCorrect(long userId, String token) {
		if (( this.id != userId ) || StringUtils.isEmpty(token))
			return false;
		long tkn = Long.parseLong(token);
		return (tkn == this.getToken());
	}

	/**
	 * returns true if the user has administrative privileges
	 * @param userName
	 * @param token
	 * @return
	 */
	@Override
	public boolean isAdmin( String userName, long token ) {
		return ( ILoginUser.S_ADMIN.equals(userName)) && ILoginUser.AdminToken == token;
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
