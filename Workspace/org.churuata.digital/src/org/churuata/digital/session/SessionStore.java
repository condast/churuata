package org.churuata.digital.session;

import org.churuata.digital.core.data.ChuruataProfileData;
import org.condast.commons.authentication.session.DefaultSessionStore;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.na.data.ContactPersonData;
import org.condast.commons.na.profile.IProfileData;

public class SessionStore<D extends Object> extends DefaultSessionStore<D>{

	private static final String S_CHURUATA_ID = "org.churuata.digital";

	private long token;
	
	private LatLng selected;
	
	private IProfileData profile; 
	
	private ContactPersonData contactPersonData;

	public SessionStore() {
		super( S_CHURUATA_ID);
	}

	public SessionStore( long token ) {
		this();
		this.token = token;
	}

	@Override
	public void clear() {
		super.clear();
	}

	public long getToken() {
		return token;
	}

	public void setToken(long token) {
		this.token = token;
	}

	public LatLng getSelected() {
		return selected;
	}

	public void setSelected(LatLng selected) {
		this.selected = selected;
	}

	public IProfileData getProfile() {
		return profile;
	}

	public void setProfile(IProfileData profile) {
		this.profile = profile;
	}

	public ContactPersonData getContactPersonData() {
		return contactPersonData;
	}

	public void setContactPersonData(ContactPersonData contactPersonData) {
		this.contactPersonData = contactPersonData;
	}	
}