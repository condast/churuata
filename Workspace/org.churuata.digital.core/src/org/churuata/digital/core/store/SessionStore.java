package org.churuata.digital.core.store;

import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.data.latlng.LatLng;

public class SessionStore {

	private ILoginUser user;
	
	private LatLng selected;
	
	public SessionStore() {
		super();
	}

	public SessionStore(ILoginUser user) {
		super();
		this.user = user;
	}

	public ILoginUser getLoginUser() {
		return user;
	}

	public void setLoginUser(ILoginUser user) {
		this.user = user;
	}

	public LatLng getSelected() {
		return selected;
	}

	public void setSelected(LatLng selected) {
		this.selected = selected;
	}	
}
