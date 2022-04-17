package org.churuata.digital.session;


import org.condast.commons.authentication.session.DefaultSessionStore;
import org.condast.commons.data.latlng.LatLng;

public class SessionStore extends DefaultSessionStore{

	private static final String S_ARNAC_ID = "org.satr.arnac";

	private long token;
	
	private LatLng selected;

	public SessionStore( long token ) {
		super( S_ARNAC_ID);
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

	
}