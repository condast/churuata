package org.churuata.rest.model;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import org.condast.commons.strings.StringStyler;
import org.condast.commons.strings.StringUtils;

public class Notification {

	public enum Events{
		IDLE,
		ILLNESS,
		APPOINTMENT,
		CONFIRMATION;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString());
		}	
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column( nullable=true)
	private String anonymous; //anonymous id

	@Column( nullable=true)
	private String notification; //could be an illness

	@ElementCollection(fetch=FetchType.EAGER)
	private Map<Long,String> events;
	
	@Basic(optional = false)
	@Column( nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;
	
	@Basic(optional = false)
	@Column( nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateDate;

	public Notification() {
		events = new HashMap<>();
		this.createDate = Calendar.getInstance().getTime();
		this.updateDate = Calendar.getInstance().getTime();
	}

	public Notification(String anonymous, String notification) {
		this();
		this.anonymous = anonymous;
		this.notification = notification;
	}

	public long getId() {
		return id;
	}

	public String getAnonymous() {
		return anonymous;
	}

	public String getNotification() {
		return notification;
	}

	public Events getEvent( Date date ) {
		String str = this.events.get(date.getTime());
		return StringUtils.isEmpty(str)?Events.IDLE: Events.valueOf( str );
	}

	public void putEvent( Date date, Events event ) {
		this.events.put(date.getTime(), event.name());
	}

	protected Map<Long, String> getEvents() {
		return events;
	}
}
