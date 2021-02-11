package org.churuata.digital.core.store;

import org.condast.commons.authentication.user.ILoginUser;

public class SessionStore {

	private ILoginUser user;
	
	public SessionStore() {
		super();
	}

	public SessionStore(ILoginUser user) {
		super();
		this.user = user;
	}

	public ILoginUser getUser() {
		return user;
	}
	
	
}
