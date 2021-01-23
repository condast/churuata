package org.churuata.rest.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

import org.condast.commons.authentication.user.ILoginUser;

@Entity
public class UserData{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	private long userid;
		
	@OneToMany( cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Location> features;
	private int selectedLocation;
	
	@Basic(optional = true)
	private String description;

	@Basic(optional = true)
	@Column( nullable=true)
	private String options;

	@Basic(optional = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;

	private transient ILoginUser user;
	
	public UserData() {
		super();
	}

	public UserData( ILoginUser user, String description) {
		super();
		this.selectedLocation = 0;
		this.userid = ( user == null )?0: user.getId();
		this.user = user;
		this.features = new ArrayList<>();
		this.description = description;
		this.createDate = Calendar.getInstance().getTime();
	}

	/* (non-Javadoc)
	 * @see org.fgf.animal.count.location.model.IBatch#getId()
	 */
	public long getId() {
		return id;
	}

	public long getUserId() {
		return userid;
	}

	public ILoginUser getLoginUser() {
		return user;
	}

	public void setLoginUser( ILoginUser user ) {
		this.user = user;
	}

	/* (non-Javadoc)
	 * @see org.fgf.animal.count.location.model.IBatch#getUserName()
	 */
	public String getUserName() {
		return (user == null )? null: user.getUserName();
	}

	/* (non-Javadoc)
	 * @see org.fgf.animal.count.location.model.IBatch#getDescription()
	 */
	public String getDescription() {
		return description;
	}

	/* (non-Javadoc)
	 * @see org.fgf.animal.count.location.model.IBatch#setDescription(java.lang.String)
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	public Date getCreateDate() {
		return createDate;
	}
}